package com.hoan.client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoan.client.adapter.MessagesRecycleViewAdapter
import com.hoan.client.databinding.ActivityMessageBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.request.SendMessageRequest
import com.hoan.client.network.response.ConversationDetailResponse
import com.hoan.client.network.response.RecentMessages
import com.hoan.client.network.response.UserResponse
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MessageActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }

    private lateinit var binding: ActivityMessageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var messagesAdapter: MessagesRecycleViewAdapter
    private lateinit var pusher: Pusher
    private val messageList = mutableListOf<RecentMessages>()
    private var conversationId: Long = 0L
    private var currentUserId: Long = 0L
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferences = getSharedPreferences("user_shared_preference", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)
        currentUserId = sharedPreferences.getLong("userId", 0L)


        conversationId = intent.getLongExtra("conversationId", 0L)

        setupToolbar()
        setupRecyclerView()
        fetchConversation()
        setupPusherCloud()


        binding.ivAttachImage.setOnClickListener { pickImage() }
        binding.sendBtn.setOnClickListener { sendMessage() }
        binding.btnClearImage.setOnClickListener {
            imageUri = null
            binding.flImagePreviewContainer.visibility = View.GONE
            binding.editTextMessage.hint = getString(R.string.enter_message)
        }
    }

    private fun setupToolbar() {
        binding.ivBackButton.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesRecycleViewAdapter(this, messageList)
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MessageActivity)
            adapter = messagesAdapter
        }
    }

    private fun fetchConversation() {
        RetrofitInstance.messageService.getConversationDetail(conversationId)
            .enqueue(object : Callback<ConversationDetailResponse> {
                override fun onResponse(
                    call: Call<ConversationDetailResponse>,
                    response: Response<ConversationDetailResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()!!.let { body ->
                            messageList.clear()
                            binding.tvMessageTitle.text = body.name
                            messageList.addAll(body.messages)
                            messagesAdapter.updateMessages(messageList)
                            scrollToBottom()
                        }
                    } else {
                        Toast.makeText(
                            this@MessageActivity,
                            "Lỗi khi tải tin nhắn: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ConversationDetailResponse>, t: Throwable) {
                    Toast.makeText(
                        this@MessageActivity,
                        "Không thể kết nối máy chủ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.ivImagePreview.setImageURI(imageUri)
            binding.flImagePreviewContainer.visibility = View.VISIBLE
            binding.editTextMessage.hint = getString(R.string.hint_image_preview)
        }
    }

    private fun sendMessage() {
        val text = binding.editTextMessage.text.toString().trim()
        binding.sendBtn.isEnabled = false

        val uriToSend = imageUri
        imageUri = null
        binding.flImagePreviewContainer.visibility = View.GONE
        binding.editTextMessage.hint = getString(R.string.enter_message)

        val convBody = conversationId.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        if (uriToSend != null) {
            val file = File(getRealPathFromURI(uriToSend))
            val bodyFile = MultipartBody.Part.createFormData(
                "file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
            )
            val textBody = text.toRequestBody("text/plain".toMediaTypeOrNull())
            RetrofitInstance.messageService
                .sendMessageWithTextAndImage(convBody, textBody, bodyFile)
                .enqueue(messageSendCallback())
        } else if (text.isNotEmpty()) {
            RetrofitInstance.messageService
                .sendTextMessage(SendMessageRequest(conversationId, text))
                .enqueue(messageSendCallback())
        } else {
            binding.sendBtn.isEnabled = true
        }
    }

    private fun messageSendCallback() = object : Callback<RecentMessages> {
        override fun onResponse(call: Call<RecentMessages>, response: Response<RecentMessages>) {
            binding.sendBtn.isEnabled = true
            response.body()?.let { newMsg ->
                messageList.add(newMsg)
                messagesAdapter.updateMessages(messageList)
                scrollToBottom()
                binding.editTextMessage.text?.clear()
            } ?: run {
                Toast.makeText(
                    this@MessageActivity,
                    "Gửi thất bại: ${response.message()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onFailure(call: Call<RecentMessages>, t: Throwable) {
            binding.sendBtn.isEnabled = true
            Toast.makeText(
                this@MessageActivity,
                "Không thể gửi tin nhắn",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun scrollToBottom() {
        binding.messagesRecyclerView.post {
            binding.messagesRecyclerView.scrollToPosition(messageList.size - 1)
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, proj, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            }
        }
        return path
    }

    private fun setupPusherCloud() {
        val options = PusherOptions()
            .setCluster("ap1")
            .setEncrypted(true)

        pusher = Pusher("7a3acf39b09391fdd495", options)
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.d("PusherCloud", "State: ${change.previousState} → ${change.currentState}")
            }
            override fun onError(message: String?, code: String?, e: Exception?) {
                Log.e("PusherCloud", "Error: $message (code=$code)", e)
            }
        }, ConnectionState.ALL)

        val channel = pusher.subscribe("chat.$conversationId")
        channel.bind("MessageSent", SubscriptionEventListener { event ->
            val json    = JSONObject(event.data)
            val payload = json.optJSONObject("message") ?: json

            val senderId = payload.getLong("sender_id")
            if (senderId == currentUserId) return@SubscriptionEventListener

            Log.d("PusherCloud", "Sender ID: $senderId, Current User ID: $currentUserId")

            val sj = payload.getJSONObject("sender")
            val sender = UserResponse(
                id               = sj.getLong("id"),
                username         = sj.getString("username"),
                fullName         = sj.getString("fullName"),
                email            = sj.optString("email"),
                biography        = sj.optString("biography"),
                location         = sj.optString("location"),
                profilePicture   = sj.optString("profile_picture"),
                registration_date= sj.optString("registration_date")
            )
            val newMsg = RecentMessages(
                id              = payload.getInt("id"),
                sender          = sender,
                content         = payload.optString("content", null),
                image_url       = payload.optString("image_url", null),
                createdAt       = payload.getString("created_at"),
                isSender        = false,
                conversation_id = payload.getInt("conversation_id")
            )

            Log.d("PusherCloud", "New message: $newMsg")
            runOnUiThread {
                messageList.add(newMsg)
                messagesAdapter.updateMessages(messageList)
                scrollToBottom()
            }
        })
    }
}
