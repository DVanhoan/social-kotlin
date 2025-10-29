package com.hoan.client

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hoan.client.databinding.ActivityFeedBinding
import com.hoan.client.fragment.*
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.JwtResponse
import com.hoan.client.network.response.UserResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedActivity : AppCompatActivity(), EditProfileFragment.EditedUserListener {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: UserResponse
    private var userList: List<UserResponse> = emptyList()
    private val sharedPrefName = "user_shared_preference"
    private val picasso: Picasso by lazy { Picasso.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferences = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        RetrofitInstance.setToken(sharedPreferences.getString("jwt", "") ?: "")
        getUser()

        setupBottomNavigation()
    }


    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(ListPostsFragment.newInstance(user), TAG_HOME)
                    true
                }
                R.id.navigation_search -> {
                    if (userList.isEmpty()) {
                        loadUserList { success ->
                            if (success) {
                                replaceFragment(FriendFragment.newInstance(userList), TAG_SEARCH)
                            }
                        }
                    } else {
                        replaceFragment(FriendFragment.newInstance(userList), TAG_SEARCH)
                    }
                    true
                }
                R.id.navigation_chats -> {
                    replaceFragment(ChatFragment.newInstance(), TAG_CHATS)
                    true
                }
                R.id.navigation_notifications -> {
                    replaceFragment(NotificationFragment.newInstance(), TAG_NOTIFS)
                    true
                }
                R.id.navigation_profile -> {
                    if (::user.isInitialized) {
                        replaceFragment(ProfileFragment.newInstance(user), TAG_PROFILE)
                    } else {
                        getUser()
                    }
                    true
                }
                else -> false
            }
        }
    }



    private fun getUser() {
        RetrofitInstance.userService.getUser().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onGetUserSuccess(response.body()!!)
                } else if (response.code() == 401) {
                    refreshToken()
                } else {
                    handleAuthError(response.message())
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "getUser failure", t)
            }
        })
    }

    private fun onGetUserSuccess(responseBody: UserResponse) {
        user = responseBody

        binding.bottomNavigation.selectedItemId = R.id.navigation_home

        loadUserList(null)
    }


    // Gọi API refresh JWT rồi recall getUser()
    private fun refreshToken() {
        RetrofitInstance.userService.refreshToken().enqueue(object : Callback<JwtResponse> {
            override fun onResponse(call: Call<JwtResponse>, response: Response<JwtResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val jwt = response.body()!!.jwt
                    val expirationTime = System.currentTimeMillis() + response.body()!!.expires_in.toLong() * 1000L
                    sharedPreferences.edit()
                        .putString("jwt", jwt)
                        .putLong("expiration_time", expirationTime)
                        .apply()
                    RetrofitInstance.setToken(jwt)
                    getUser()
                } else {
                    handleAuthError("Refresh token failed")
                }
            }
            override fun onFailure(call: Call<JwtResponse>, t: Throwable) {
                Log.e(TAG, "refreshToken failure", t)
                handleAuthError("Network error")
            }
        })
    }


    private fun handleAuthError(msg: String) {
        Log.e(TAG, "Auth error: $msg")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun loadUserList(callback: ((Boolean) -> Unit)?) {
        RetrofitInstance.userService.loadUserList()
            .enqueue(object : Callback<List<UserResponse>> {
                override fun onResponse(
                    call: Call<List<UserResponse>>,
                    response: Response<List<UserResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        userList = response.body()!!
                        callback?.invoke(true)
                    } else {
                        callback?.invoke(false)
                    }
                }
                override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                    Log.e(TAG, "loadUserList failure", t)
                    callback?.invoke(false)
                }
            })
    }

    /**
     * Thay thế Fragment trong container.
     * Trước khi replace, luôn show lại BottomNavigationView.
     */
    private fun replaceFragment(fragment: Fragment, tag: String) {
        binding.bottomNavigation.visibility = View.VISIBLE

        val tx = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach { tx.hide(it) }
        supportFragmentManager.findFragmentByTag(tag)?.let {
            tx.show(it)
        } ?: run {
            tx.add(R.id.fragment_container_view, fragment, tag)
        }
        tx.commit()
    }


    fun addFullscreenFragment(fragment: Fragment, tag: String) {
        binding.bottomNavigation.visibility = View.GONE
        val tx = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach { tx.hide(it) }
        supportFragmentManager.findFragmentByTag(tag)?.let {
            tx.show(it)
        } ?: run {
            tx.add(R.id.fragment_container_view, fragment, tag)
        }
        tx.addToBackStack(tag)
        tx.commit()
    }

    companion object {
        private const val TAG = "FeedActivity"
        private const val TAG_HOME = "HOME"
        private const val TAG_SEARCH = "SEARCH"
        private const val TAG_CHATS = "CHATS"
        private const val TAG_NOTIFS = "NOTIFS"
        private const val TAG_PROFILE = "PROFILE"
    }

    override fun updateUserDetails(user: UserResponse) {
        Log.d("UPDATING_USER_DETAILS", user.toString())
        this.user = user
    }

}
