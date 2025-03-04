package com.hoan.client

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.hoan.client.database.ImageCacheDatabase
import com.hoan.client.database.repository.CacheService
import com.hoan.client.databinding.ActivityFeedBinding
import com.hoan.client.fragment.FriendFragment
import com.hoan.client.fragment.ListPostsFragment
import com.hoan.client.fragment.NewPostFragment
import com.hoan.client.fragment.ProfileFragment
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.constant.Constants
import com.hoan.client.fragment.EditProfileFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedActivity : AppCompatActivity(), EditProfileFragment.EditedUserListener {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: UserResponse
    private var canUserPost: Boolean = false
    private val sharedPrefName = "user_shared_preference"

    private val cache: CacheService = CacheService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""

        RetrofitInstance.setToken(token)


        cache.initDatabase(
            Room.databaseBuilder(
                applicationContext,
                ImageCacheDatabase::class.java,
                "image-cache"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        )

        getUser()
        loadUserList()

        binding.toolbar.visibility = View.VISIBLE
        Constants.showSuccessSnackbar(this, layoutInflater, "Successful login!")
    }

    private fun getUser() {
        RetrofitInstance.userService.getUser()
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getUserSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error retrieving user: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun getUserSuccess(statusCode: Int, responseBody: UserResponse) {
        Log.d("GET_USER", "Successfully queried user: $responseBody Status code: $statusCode")
        user = responseBody
        loadProfileData(user)
        canUserPost()
    }

    fun getCurrentUser(): UserResponse = user

    private fun loadUserList() {
        RetrofitInstance.userService.loadUserList()
            .enqueue(object : Callback<List<UserResponse>> {
                override fun onResponse(
                    call: Call<List<UserResponse>>,
                    response: Response<List<UserResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        onLoadUsersSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error loading user list: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun onLoadUsersSuccess(statusCode: Int, responseBody: List<UserResponse>) {
        Log.d("USER_LIST", "Successful loadUserList call. $responseBody Status code: $statusCode")
        binding.btnAddFriend.isClickable = true
        Log.d("USER_LIST", responseBody.toString())
        val fragment: Fragment = FriendFragment.newInstance(responseBody)
        binding.btnAddFriend.setOnClickListener {
            findViewById<View>(R.id.toolbar).visibility = View.GONE
            addFullscreenFragment(fragment, "ADD_FRIENDS")
        }
    }

    private fun loadProfileData(user: UserResponse) {
        cache.cacheProfilePicture(user, binding.btnProfile)
        binding.btnProfile.isClickable = true
        binding.btnProfile.setOnClickListener {
            addFullscreenFragment(ProfileFragment.newInstance(user), "PROFILE_FRAGMENT")
        }
    }

    private fun canUserPost() {
        RetrofitInstance.postService.canUserPost()
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful && response.body() != null) {
                        canUserPostSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error checking post permission: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun canUserPostSuccess(statusCode: Int, responseBody: Boolean) {
        Log.d("CAN_USER_POST", "Successful canUserPost call. $responseBody Status code: $statusCode")
        canUserPost = responseBody

        replaceFragment(ListPostsFragment.newInstance(user), "LIST_POST_FRAGMENT")
        if (canUserPost)
            replaceFragment(NewPostFragment.newInstance(user), "NEW_POST_FRAGMENT")
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        Log.d("FRAGMENT", "Adding fragment ${fragment.id} with tag $tag")
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.hoan.client.R.id.fragment_container_view, fragment, tag)
        fragmentTransaction.addToBackStack(fragment.id.toString())
        fragmentTransaction.commit()
    }

    private fun addFullscreenFragment(fragment: Fragment, tag: String) {
        binding.toolbar.visibility = View.GONE
        replaceFragment(fragment, tag)
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call!")
        e.printStackTrace()
    }

    override fun updateUserDetails(user: UserResponse) {
        loadProfileData(user)
    }
}
