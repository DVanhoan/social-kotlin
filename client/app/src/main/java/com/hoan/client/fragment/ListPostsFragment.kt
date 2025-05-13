package com.hoan.client.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hoan.client.NewPostActivity
import com.hoan.client.R
import com.hoan.client.adapter.PostsRecyclerViewAdapter
import com.hoan.client.constant.Constants
import com.hoan.client.databinding.FragmentListPostsBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.UserResponse
import com.hoan.client.viewmodel.PostsViewModel
import androidx.activity.result.ActivityResult

class ListPostsFragment(private val user: UserResponse) : Fragment() {

    private var _binding: FragmentListPostsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostsViewModel by viewModels()
    private lateinit var adapter: PostsRecyclerViewAdapter

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"
    private lateinit var imagePicker: ImagePicker.Builder

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPostsBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity()
            .getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        RetrofitInstance.setToken(sharedPreferences.getString("jwt", "").orEmpty())


        imagePicker = ImagePicker.with(requireActivity())
            .compress(1024)
            .maxResultSize(720, 1080)
            .galleryMimeTypes(arrayOf("image/png", "image/jpg", "image/jpeg"))

        setupRecyclerView()
        setupUiActions()
        observeViewModel()
        viewModel.loadPosts()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = PostsRecyclerViewAdapter(
            currentUserId   = user.id,
            reactionListener = object : PostsRecyclerViewAdapter.ReactionListener {
                override fun reaction(postId: Long) {
                    showReactionPicker(postId)
                }
            },
            settingsListener = object : PostsRecyclerViewAdapter.SettingsListener {
                override fun onEditPost(post: PostResponse) {

                }
                override fun onDeletePost(post: PostResponse) {}
                override fun onReportPost(post: PostResponse) { }
            }
        )
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postsRecyclerView.adapter = adapter
    }

    private fun setupUiActions() {
        binding.tvStatus.setOnClickListener { navigateToNewPost(null) }
        binding.btnImage.setOnClickListener {
            if (!checkPermissions()) return@setOnClickListener
            val options = arrayOf("Chụp ảnh", "Chọn ảnh từ thư viện")
            AlertDialog.Builder(requireContext())
                .setTitle("Chọn nguồn ảnh")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> imagePicker.cameraOnly().createIntent { pickImageLauncher.launch(it) }
                        1 -> imagePicker.galleryOnly().createIntent { pickImageLauncher.launch(it) }
                    }
                }
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.posts.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { err ->
            err?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        })
    }

    private fun showReactionPicker(postId: Long) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reaction_picker, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val reactionMap = mapOf(
            R.id.btn_react_like  to "like",
            R.id.btn_react_love  to "love",
            R.id.btn_react_haha  to "haha",
            R.id.btn_react_sad   to "sad",
            R.id.btn_react_tired to "tired"
        )
        reactionMap.forEach { (btnId, reactionType) ->
            dialogView.findViewById<ImageButton>(btnId).setOnClickListener {
                dialog.dismiss()
                viewModel.react(postId, reactionType)
            }
        }
        dialog.show()
    }

    private fun navigateToNewPost(imageUri: String?) {
        val i = Intent(requireContext(), NewPostActivity::class.java)
        imageUri?.let { i.putExtra("imageUri", it) }
        startActivity(i)
    }

    private fun checkPermissions(): Boolean {
        val perms = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED)
            perms += Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
            perms += Manifest.permission.ACCESS_FINE_LOCATION

        return if (perms.isEmpty()) true
        else {
            ActivityCompat.requestPermissions(requireActivity(), perms.toTypedArray(), 123)
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(user: UserResponse) = ListPostsFragment(user)
    }
}
