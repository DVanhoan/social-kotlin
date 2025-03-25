package com.hoan.client.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.adapter.UsersRecyclerViewAdapter
import com.hoan.client.databinding.FragmentMyFriendsBinding
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFriendsFragment : Fragment(R.layout.fragment_my_friends) {

    private var _binding: FragmentMyFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"
    private var userId: Long = -1L

    private lateinit var friendsRecyclerViewAdapter: UsersRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyFriendsBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        userId = sharedPreferences.getLong("userId", -1)
        RetrofitInstance.setToken(token)

        setupFriendsRecyclerView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.toolbar).visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
    }

    private fun setupFriendsRecyclerView() {
        val llm = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        friendsRecyclerViewAdapter = UsersRecyclerViewAdapter(
            UsersRecyclerViewAdapter.ListType.FRIEND,
            requireActivity()
        )
        val list = binding.root.findViewById<RecyclerView>(R.id.friend_list_recycler_view)
        list.layoutManager = llm
        list.adapter = friendsRecyclerViewAdapter

        loadFriendList()
    }

    private fun loadFriendList() {
        RetrofitInstance.friendService.getListOfFriends().enqueue(object : Callback<List<Long>> {
            override fun onResponse(call: Call<List<Long>>, response: Response<List<Long>>) {
                if (response.isSuccessful && response.body() != null) {
                    getFriendListSuccess(response.code(), response.body()!!)
                } else {
                    generalError(response.code(), Exception("Error loading friend list: ${response.message()}"))
                }
            }
            override fun onFailure(call: Call<List<Long>>, t: Throwable) {
                generalError(500, t)
            }
        })
    }

    private fun getFriendListSuccess(statusCode: Int, responseBody: List<Long>) {
        Log.d("FRIEND_LIST", "Successfully queried friend list: $responseBody Status code: $statusCode")
        if (responseBody.isEmpty()) {
            binding.noFriends.visibility = View.VISIBLE
            binding.friendListRecyclerView.visibility = View.GONE
        } else {
            binding.noFriends.visibility = View.GONE
            binding.friendListRecyclerView.visibility = View.VISIBLE
        }
        responseBody.forEach { getUserByUserId(it) }
    }

    private fun getUserByUserId(userId: Long) {
        RetrofitInstance.userService.getUserByUserId(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    getUserByUserIdSuccess(response.code(), response.body()!!)
                } else {
                    generalError(response.code(), Exception("Error loading user by userId: ${response.message()}"))
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                generalError(500, t)
            }
        })
    }

    private fun getUserByUserIdSuccess(statusCode: Int, responseBody: UserResponse) {
        Log.d("USER_BY_USERID", "Successfully queried user: $responseBody Status code: $statusCode")
        friendsRecyclerViewAdapter.add(responseBody)
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call!")
        e.printStackTrace()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyFriendsFragment()
    }
}
