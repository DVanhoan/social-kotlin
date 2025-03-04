package com.hoan.client.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hoan.client.R
import com.hoan.client.constant.Constants
import com.hoan.client.databinding.FragmentNewPostBinding
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class NewPostFragment(private val user: UserResponse) : Fragment(R.layout.fragment_new_post) {

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    // Không cần khai báo "network" riêng; sử dụng RetrofitInstance.
    private lateinit var imagePicker: ImagePicker.Builder
    private var mainPhoto: MultipartBody.Part? = null
    private var selfiePhoto: MultipartBody.Part? = null

    private val sharedPrefName = "user_shared_preference"

    private var locationPermission: Boolean = false
    private var cameraPermission: Boolean = false

    // Sử dụng ActivityResultContracts để nhận kết quả từ ImagePicker.
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    val multipart = getMultiPartImageFromUri(fileUri)
                    // Nếu mainPhoto chưa có thì set mainPhoto, ngược lại set selfiePhoto.
                    if (mainPhoto == null) mainPhoto = multipart else selfiePhoto = multipart
                    // Khi đã có đủ 2 ảnh, gọi API tạo post.
                    if (selfiePhoto != null) {
                        createPost(mainPhoto!!, selfiePhoto!!)
                        mainPhoto = null
                        selfiePhoto = null
                        Log.d("CREATING_POST", "Creating post")
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    mainPhoto = null
                    selfiePhoto = null
                    Constants.showErrorSnackbar(
                        requireContext(),
                        layoutInflater,
                        ImagePicker.getError(data)
                    )
                }
                else -> {
                    mainPhoto = null
                    selfiePhoto = null
                    Constants.showErrorSnackbar(
                        requireContext(),
                        layoutInflater,
                        "Upload Cancelled"
                    )
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lấy token từ SharedPreferences và set cho RetrofitInstance.
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        // Khởi tạo ImagePicker.
        imagePicker = ImagePicker.with(requireActivity())
            .crop(2F, 3F)
            .compress(1024)
            .maxResultSize(720, 1080)
            .cameraOnly()
            .galleryMimeTypes(arrayOf("image/png", "image/jpg", "image/jpeg"))

        // Đăng ký callback cho nút Back.
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        checkPermissions()
    }

    private fun checkPermissions() {
        locationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!locationPermission || !cameraPermission) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("PERMISSIONS", "Location Permission Granted")
                        locationPermission = true
                    }
                } else {
                    Log.d("PERMISSIONS", "Location Permission Denied")
                    locationPermission = false
                }
                if (grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("PERMISSIONS", "Camera Permission Granted")
                        cameraPermission = true
                    }
                } else {
                    Log.d("PERMISSIONS", "Camera Permission Denied")
                    cameraPermission = false
                }
                return
            }
        }
    }

    private fun getMultiPartTextFromString(text: String): RequestBody =
        text.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun getMultipartLocation(): RequestBody {
        // TODO: Lấy vị trí thực tế của người dùng thay vì "Budapest".
        return getMultiPartTextFromString("Budapest")
    }

    private fun getMultiPartImageFromUri(uri: Uri): MultipartBody.Part {
        val file = File(uri.path!!)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartName = if (mainPhoto == null) "main" else "selfie"
        return MultipartBody.Part.createFormData(multipartName, file.name, requestBody)
    }

    private fun createPost(mainPhoto: MultipartBody.Part, selfiePhoto: MultipartBody.Part) {
        val location = getMultipartLocation()
        RetrofitInstance.postService.createPost(mainPhoto, selfiePhoto, location)
            .enqueue(object : retrofit2.Callback<PostResponse> {
                override fun onResponse(
                    call: retrofit2.Call<PostResponse>,
                    response: retrofit2.Response<PostResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        createPostSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error creating post: ${response.message()}"))
                    }
                }
                override fun onFailure(call: retrofit2.Call<PostResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun createPostSuccess(statusCode: Int, responseBody: PostResponse) {
        Log.d("CREATE_POST_SUCCESS", "Post created successfully: $responseBody, Status code: $statusCode")
        val fragment = ListPostsFragment.newInstance(user)
        popBackStack()
        replaceFragment(fragment, "LIST_POST_FRAGMENT")
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call!")
        e.printStackTrace()
    }

    private fun popBackStack() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        Log.d("FRAGMENT_REPLACE", "Replacing fragment with tag: $tag")
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, fragment, tag)
        requireActivity().findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
        fragmentTransaction.addToBackStack(fragment.id.toString())
        fragmentTransaction.commit()
    }

    private val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val toolbar: View? = requireActivity().findViewById(R.id.toolbar)
            if (toolbar?.visibility == View.VISIBLE) {
                requireActivity().finish()
            } else {
                toolbar?.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Chỉ có 1 onCreateView được giữ lại.
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.postBefakeButton.setOnClickListener {
            checkPermissions()
            if (cameraPermission && locationPermission) {
                // Gọi ImagePicker 2 lần để chụp 2 ảnh: main photo và selfie.
                imagePicker.createIntent { intent -> startForProfileImageResult.launch(intent) }
                imagePicker.createIntent { intent -> startForProfileImageResult.launch(intent) }
            } else {
                Constants.showErrorSnackbar(
                    requireContext(),
                    layoutInflater,
                    "Permissions are required to capture images"
                )
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(user: UserResponse) = NewPostFragment(user)
    }
}
