package com.hoan.client.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hoan.client.NewPostActivity
import com.hoan.client.R
import com.hoan.client.adapter.PostsRecyclerViewAdapter
import com.hoan.client.adapter.PostsRecyclerViewAdapter.ReactionListener
import com.hoan.client.adapter.PostsRecyclerViewAdapter.SettingsListener
import com.hoan.client.constant.Constants
import com.hoan.client.constant.ImagePickType
import com.hoan.client.databinding.FragmentListPostsBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.ReactionResponse
import com.hoan.client.network.response.UserResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListPostsFragment(private var user: UserResponse) :
    Fragment(R.layout.fragment_list_posts),
    ReactionListener {

    private var _binding: FragmentListPostsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var postsRecyclerViewAdapter: PostsRecyclerViewAdapter

    private var currentPickType: ImagePickType? = null

    private lateinit var imagePicker: ImagePicker.Builder

    private var reactionOnPostID: Long? = null
    private var reactionPosition: Int? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val fileUri = result.data?.data
                if (fileUri != null) {
                    navigateToNewPost(fileUri.toString())
                }
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Constants.showErrorSnackbar(
                    requireContext(),
                    layoutInflater,
                    ImagePicker.getError(result.data)
                )
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPostsBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences("user_shared_preference", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        imagePicker = ImagePicker.with(requireActivity())
            .compress(1024)
            .maxResultSize(720, 1080)
            .galleryMimeTypes(arrayOf("image/png", "image/jpg", "image/jpeg"))

        setupRecyclerView()
        getAllPosts()


        binding.tvStatus.setOnClickListener {
            navigateToNewPost(null)
        }


        binding.btnImage.setOnClickListener {
            if (!checkPermissions()) return@setOnClickListener

            val options = arrayOf("Chụp ảnh", "Chọn ảnh từ thư viện")
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Chọn nguồn ảnh")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> imagePicker.cameraOnly().createIntent { pickImageLauncher.launch(it) }
                        1 -> imagePicker.galleryOnly().createIntent { pickImageLauncher.launch(it) }
                    }
                }
                .show()
        }

        return binding.root
    }


    private fun navigateToNewPost(imageUri: String?) {
        val intent = Intent(requireContext(), NewPostActivity::class.java)
        imageUri?.let { intent.putExtra("imageUri", it) }
        startActivity(intent)
    }


    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.postsRecyclerView.layoutManager = layoutManager
        postsRecyclerViewAdapter = PostsRecyclerViewAdapter(
            currentUserId = user.id,
            reactionListener = this,
            settingsListener = object : SettingsListener {
                override fun onEditPost(post: PostResponse) {
                    Log.d("SETTINGS", "Edit post: $post")
                }

                override fun onDeletePost(post: PostResponse) {
                    Log.d("SETTINGS", "Delete post: $post")
                }

                override fun onReportPost(post: PostResponse) {
                    Log.d("SETTINGS", "Report post: $post")
                }
            },
            activity = requireActivity()
        )
        binding.postsRecyclerView.adapter = postsRecyclerViewAdapter
    }


    private fun getAllPosts() {
        RetrofitInstance.postService.getAllPosts()
            .enqueue(object : Callback<List<PostResponse>> {
                override fun onResponse(
                    call: Call<List<PostResponse>>,
                    response: Response<List<PostResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val list = response.body()!!
                        Log.d("POSTS", list.toString())
                        postsRecyclerViewAdapter.setPosts(list)
                    } else {
                        showError("Error fetching posts: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<PostResponse>>, t: Throwable) {
                    showError("Error fetching posts: ${t.localizedMessage}")
                }
            })
    }

    private fun checkPermissions(): Boolean {
        val needLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val needCamera = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        val permissionsToRequest = mutableListOf<String>()
        if (needLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (needCamera != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        return if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                123
            )
            false
        } else true
    }

    private fun showError(msg: String) {
        Constants.showErrorSnackbar(requireContext(), layoutInflater, msg)
    }

    override fun reaction(postId: Long, position: Int) {
        reactionOnPostID = postId
        reactionPosition = position
        currentPickType = ImagePickType.REACTION

        imagePicker.cameraOnly().createIntent { pickImageLauncher.launch(it) }
    }

    private fun react(postId: Long, position: Int, reactionPart: MultipartBody.Part) {
        RetrofitInstance.reactionService.react(reactionPart, postId)
            .enqueue(object : Callback<ReactionResponse> {
                override fun onResponse(
                    call: Call<ReactionResponse>,
                    response: Response<ReactionResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        postsRecyclerViewAdapter.notifyItemChanged(position + 1)
                    } else {
                        showError("Error reacting: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ReactionResponse>, t: Throwable) {
                    showError("Error reacting: ${t.localizedMessage}")
                }
            })
        reactionOnPostID = null
        reactionPosition = null
    }


    fun updateUserDetails(user: UserResponse) {
        this.user = user
        postsRecyclerViewAdapter.updateUser(user)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (requireActivity().supportFragmentManager.fragments.size == 1) {
                        requireActivity().finish()
                    } else {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(user: UserResponse) = ListPostsFragment(user)
    }
}
