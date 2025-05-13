package com.hoan.client

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoan.client.adapter.MemberAdapter
import com.hoan.client.databinding.ActivityCreateConversationBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.CreateConversationResponse
import com.hoan.client.network.response.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateConversationBinding
    private lateinit var memberAdapter: MemberAdapter
    private val selectedMemberIds = mutableSetOf<Long>()
    private val allMembers = mutableListOf<UserResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RetrofitInstance.userService.loadUserList()
            .enqueue(object : Callback<List<UserResponse>> {
                override fun onResponse(
                    call: Call<List<UserResponse>>,
                    resp: Response<List<UserResponse>>
                ) {
                    resp.body()?.let {
                        allMembers.clear()
                        allMembers.addAll(it)
                        setupMembersRecycler()
                    }
                }

                override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@CreateConversationActivity,
                        "Không tải được danh sách",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        binding.ivBackButton.setOnClickListener { finish() }

        binding.btnCreate.setOnClickListener {
            val nameInput = binding.etConversationName.text.toString().trim()

            val totalParticipants = selectedMemberIds.size + 1

            val type = if (totalParticipants == 2) "single" else "group"
            val name = when {
                type == "single" -> {
                    val otherId = selectedMemberIds.first()
                    allMembers.find { it.id == otherId }?.fullName
                        ?: allMembers.find { it.id == otherId }?.username
                        ?: "Chat"
                }

                nameInput.isNotEmpty() -> nameInput
                else -> {
                    Toast.makeText(this, "Hãy nhập tên cuộc trò chuyện", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val payload = mapOf(
                "name" to name,
                "type" to type,
                "members" to selectedMemberIds.toList()
            )

            RetrofitInstance.messageService.createConversation(payload)
                .enqueue(object : Callback<CreateConversationResponse> {
                    override fun onResponse(
                        call: Call<CreateConversationResponse>,
                        response: Response<CreateConversationResponse>
                    ) {
                        if (response.isSuccessful) {
                            val convId = response.body()!!.conversationId
                            Toast.makeText(
                                this@CreateConversationActivity,
                                "Tạo cuộc trò chuyện thành công",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@CreateConversationActivity, MessageActivity::class.java).apply {
                                putExtra("conversationId", convId)
                            }
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@CreateConversationActivity,
                                "Lỗi: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<CreateConversationResponse>, t: Throwable) {
                        Toast.makeText(
                            this@CreateConversationActivity,
                            "Không thể tạo cuộc trò chuyện",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

        private fun setupMembersRecycler() {
        memberAdapter = MemberAdapter(allMembers, selectedMemberIds)
        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(this@CreateConversationActivity)
            adapter = memberAdapter
        }
    }
}
