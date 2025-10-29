package com.hoan.client

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hoan.client.constant.Constants
import com.hoan.client.databinding.ActivityNewPostBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.PostResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class NewPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPostBinding

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    selectedImageUri = uri
                    binding.ivSelectedImage.apply {
                        visibility = View.VISIBLE
                        Glide.with(this@NewPostActivity).load(uri).into(this)
                    }
                }
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                val error = ImagePicker.getError(result.data)
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_shared_preference", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        if (intent.hasExtra("imageUri")) {
            val uriString = intent.getStringExtra("imageUri")
            if (!uriString.isNullOrEmpty()) {
                selectedImageUri = Uri.parse(uriString)
                binding.ivSelectedImage.apply {
                    visibility = View.VISIBLE
                    Glide.with(this@NewPostActivity).load(selectedImageUri).into(this)
                }
            }
        }

        setupEvents()
    }

    private fun setupEvents() {

        binding.ivBackButton.setOnClickListener {
            finish()
        }


        binding.ivPostButton.setOnClickListener {
            createPost()
        }


        binding.llLiveVideo.setOnClickListener {
            Toast.makeText(this, "Live video clicked!", Toast.LENGTH_SHORT).show()
        }


        binding.llPhotoVideo.setOnClickListener {
            showImageSourceOptions()
        }


        binding.llReel.setOnClickListener {
            Toast.makeText(this, "Reel clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImageSourceOptions() {
        val imagePickerBuilder = ImagePicker.with(this)
            .compress(1024)
            .galleryMimeTypes(arrayOf("image/png", "image/jpg", "image/jpeg"))

        val options = arrayOf("Chụp ảnh", "Chọn ảnh từ thư viện")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chọn nguồn ảnh")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        imagePickerBuilder.cameraOnly().createIntent { intent ->
                            pickImageLauncher.launch(intent)
                        }
                    }
                    1 -> {
                        imagePickerBuilder.galleryOnly().createIntent { intent ->
                            pickImageLauncher.launch(intent)
                        }
                    }
                }
            }
            .show()
    }

    private fun createPost() {
        binding.ivPostButton.visibility = View.INVISIBLE
        binding.progressSending.visibility = View.VISIBLE


        val content = binding.etPostContent.text.toString().trim()

        if (content.isEmpty() && selectedImageUri == null) {
            Toast.makeText(this, "Bạn chưa nhập nội dung hoặc chọn ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
        val locationBody = "DaNang".toRequestBody("text/plain".toMediaTypeOrNull())


        val mainPhoto: MultipartBody.Part? = if (selectedImageUri != null) {
            val file = File(selectedImageUri?.path ?: "")
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("mainPhoto", file.name, requestBody)
        } else null


        RetrofitInstance.postService.createPost(
            mainPhoto,
            contentBody,
            locationBody,
        ).enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                binding.ivPostButton.visibility = View.VISIBLE
                binding.progressSending.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@NewPostActivity, "Đăng bài thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@NewPostActivity, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("NewPostActivity", "Error creating post: ${response.message()}")
                    Constants.showErrorSnackbar(
                        this@NewPostActivity,
                        layoutInflater,
                        "Error creating post: ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                binding.ivPostButton.visibility = View.VISIBLE
                binding.progressSending.visibility = View.GONE
                Constants.showErrorSnackbar(
                    this@NewPostActivity,
                    layoutInflater,
                    "Error creating post: ${t.localizedMessage}"
                )
            }
        })
    }
}
