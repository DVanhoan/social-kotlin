package com.hoan.frontend.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson

import com.hoan.frontend.R
import com.hoan.frontend.adapters.PostDisplayAdapter
import com.hoan.frontend.databinding.FragmentMainpageBinding
import com.hoan.frontend.models.entities.Post
import com.hoan.frontend.models.entities.User
import com.hoan.frontend.utils.Extensions.toast
import com.hoan.frontend.utils.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainFragment : Fragment(R.layout.fragment_mainpage){

    private lateinit var productsAdapter: PostDisplayAdapter
    private lateinit var binding: FragmentMainpageBinding
    private lateinit var postList: ArrayList<Post>
    private lateinit var categoryList: ArrayList<String>
    private var likedProducts = mutableSetOf<String>()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainpageBinding.bind(view)
        categoryList = ArrayList()
        postList = ArrayList()

        getMe()

        val postLayoutManager = GridLayoutManager(context, 1)
        productsAdapter = PostDisplayAdapter(requireContext(), postList)
        binding.rvMainPostList.layoutManager = postLayoutManager
        binding.rvMainPostList.adapter = productsAdapter
        setProductsData()


        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mainFragment -> {
                    Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.action_mainFragment_self)
                    true
                }
                R.id.profileFragment -> {
                    Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.action_mainFragment_to_signInFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setProductsData() {
        postList.add(Post(1, "https://picsum.photos/200/300", "This is a test post", "Hoan", "2021-09-01"))
        postList.add(Post(2, "https://picsum.photos/200/300", "This is a test post", "Hoan", "2021-09-01"))
    }

    private fun getMe() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getMe()
                if (response.isSuccessful) {
                    val user: User? = response.body()
                    val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("user", Gson().toJson(user))
                        apply()
                    }
                    withContext(Dispatchers.Main) {
                        requireActivity().toast("Lấy thông tin user thành công")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        requireActivity().toast("Lỗi: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    requireActivity().toast("Exception: ${e.localizedMessage}")
                }
            }
        }
    }



}




