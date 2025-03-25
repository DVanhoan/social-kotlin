package com.hoan.client

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hoan.client.databinding.ActivityFeedBinding
import com.hoan.client.fragment.FriendFragment
import com.hoan.client.fragment.ListPostsFragment
import com.hoan.client.fragment.ProfileFragment
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.fragment.EditProfileFragment
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedActivity : AppCompatActivity(), EditProfileFragment.EditedUserListener {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: UserResponse
    private val sharedPrefName = "user_shared_preference"

    private val picasso: Picasso by lazy { Picasso.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""

        RetrofitInstance.setToken(token)

        getUser()

        binding.toolbar.visibility = View.VISIBLE
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

        loadUserList()
    }


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
        replaceFragment(ListPostsFragment.newInstance(user), "LIST_POST_FRAGMENT")

        binding.btnAddFriend.isClickable = true
        Log.d("USER_LIST", responseBody.toString())

        val fragment: Fragment = FriendFragment.newInstance(responseBody)
        binding.btnAddFriend.setOnClickListener {
            findViewById<View>(R.id.toolbar).visibility = View.GONE
            addFullscreenFragment(fragment, "ADD_FRIENDS")
        }
    }

    private fun loadProfileData(user: UserResponse) {
        picasso.load(user.profilePicture).placeholder(R.color.primaryAccent).into(binding.btnProfile)
        binding.btnProfile.isClickable = true
        binding.btnProfile.setOnClickListener {
            addFullscreenFragment(ProfileFragment.newInstance(user), "PROFILE_FRAGMENT")
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        Log.d("FRAGMENT", "Adding fragment ${fragment.id} with tag $tag")
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, fragment, tag)
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

    fun getCurrentUser(): UserResponse = user
}
