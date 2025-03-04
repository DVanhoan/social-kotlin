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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hoan.client.constant.Constants
import com.hoan.client.database.repository.CacheService
import com.hoan.client.databinding.FragmentEditProfileBinding
import com.hoan.client.network.request.UserRequest
import com.hoan.client.network.response.JwtResponse
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileFragment(
    private var user: UserResponse,
    private val editUserListeners: List<EditedUserListener>
) : Fragment(com.hoan.client.R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imagePicker: ImagePicker.Builder
    private lateinit var fileUri: Uri
    private var cameraPermission: Boolean = false

    private val sharedPrefName = "user_shared_preference"

    private val cache: CacheService = CacheService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        Log.d("EDIT_PROFILE_FRAGMENT", user.toString())

        imagePicker = ImagePicker.with(requireActivity())
            .crop(2F, 3F)
            .compress(1024)
            .maxResultSize(720, 1080)
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                Log.d("ON_ACTIVITY_RESULT", "RESULT_OK")
            }
            ImagePicker.RESULT_ERROR -> {
                Log.d("ON_ACTIVITY_RESULT", "RESULT_ERROR")
            }
            else -> {
                Log.d("ON_ACTIVITY_RESULT", "TASK_CANCELLED")
            }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    fileUri = data?.data!!
                    val file = File(fileUri.path!!)

                    Log.d("EDIT_PROFILE_PICTURE", file.toString())

                    val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    Log.d("EDIT_PROFILE_PICTURE", "Content type: ${requestBody.contentType().toString()}")

                    val multipart = MultipartBody.Part.createFormData("picture", file.name, requestBody)
                    Log.d("EDIT_PROFILE_PICTURE", multipart.toString())

                    editProfilePicture(multipart)

                    binding.civProfilePicture.setImageURI(fileUri)
                    Log.d("SELECT_IMAGE", file.toString())
                }
                ImagePicker.RESULT_ERROR -> {
                    Constants.showErrorSnackbar(
                        requireContext(),
                        layoutInflater,
                        ImagePicker.getError(data)
                    )
                }
                else -> {
                    Constants.showErrorSnackbar(requireContext(), layoutInflater, "Upload Cancelled")
                }
            }
        }

    private fun checkPermissions() {
        cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!cameraPermission) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA), 1
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {
                        Log.d("PERMISSIONS", "Permission Granted")
                        cameraPermission = true
                    }
                } else {
                    Log.d("PERMISSIONS", "Permission Denied")
                    cameraPermission = false
                }
                return
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        binding.ivBackButton.setOnClickListener {
            Log.d("BACK_ARROW", "Click")
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.ivEditButton.setOnClickListener {
            Log.d("EDIT_PROFILE", "Edited")
            val userRequest = UserRequest(
                username = binding.etUsername.text.toString(),
                fullName = binding.etFullName.text.toString(),
                email = user.email,
                biography = binding.etBiography.text.toString(),
                location = binding.etLocation.text.toString()
            )
            editUser(userRequest)
        }

        binding.civProfilePicture.setOnClickListener {
            checkPermissions()
            if (cameraPermission)
                imagePicker.createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
            else Constants.showErrorSnackbar(
                requireContext(),
                layoutInflater,
                "Camera permission is required"
            )
        }

        binding.etFullName.setText(user.fullName ?: "")
        binding.etUsername.setText(user.username)
        binding.etBiography.setText(user.biography ?: "")
        binding.etLocation.setText(user.location ?: "")

        cache.cacheProfilePicture(user, binding.civProfilePicture)

        return binding.root
    }

    private fun editProfilePicture(file: MultipartBody.Part) {
        RetrofitInstance.userService.uploadProfilePicture(file)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        editProfilePictureSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error uploading profile picture: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun editProfilePictureSuccess(statusCode: Int, responseBody: UserResponse) {
        Log.d("EDIT_PROFILE_PICTURE", "Successfully edited profile picture: $responseBody Status code: $statusCode")
        if (user.profilePicture != null)
            cache.resetKey(user.profilePicture!!)
        editUserListeners.forEach { it.updateUserDetails(responseBody) }
    }

    private fun editUser(userRequest: UserRequest) {
        RetrofitInstance.userService.editUser(userRequest)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        editUserSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error editing user: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun editUserSuccess(statusCode: Int, responseBody: UserResponse) {
        Log.d("EDIT_USER", "Successfully edited user: $responseBody Status code: $statusCode")
        user = responseBody
        editUserListeners.forEach { it.updateUserDetails(user) }
        popFragment()
    }

    private fun popFragment() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_ERROR", "Error $statusCode during API call")
        e.printStackTrace()
    }

    interface EditedUserListener {
        fun updateUserDetails(user: UserResponse)
    }

    companion object {
        @JvmStatic
        fun newInstance(user: UserResponse, listeners: List<EditedUserListener>) =
            EditProfileFragment(user, listeners)
    }
}
