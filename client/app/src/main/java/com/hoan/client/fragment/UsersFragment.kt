package com.hoan.client.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.adapter.UsersRecyclerViewAdapter
import com.hoan.client.databinding.FragmentUsersBinding
import com.hoan.client.network.response.FriendshipResponse
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersFragment(private val userList: List<UserResponse>) : Fragment(R.layout.fragment_users) {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private var userId: Long = -1L

    private lateinit var pendingRecyclerViewAdapter: UsersRecyclerViewAdapter
    private lateinit var usersRecyclerViewAdapter: UsersRecyclerViewAdapter

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"

    private val sentRequests = mutableSetOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        userId = sharedPreferences.getLong("userId", -1)
        RetrofitInstance.setToken(token)

        setupUsersRecyclerView()
        setupPendingRecyclerView()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) { }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                usersRecyclerViewAdapter.notifyDataSetChanged()
            }
        })

        return binding.root
    }

    private fun setupUsersRecyclerView() {
        val llm = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        usersRecyclerViewAdapter =
            UsersRecyclerViewAdapter(UsersRecyclerViewAdapter.ListType.USER, requireActivity())
        val list = binding.root.findViewById<RecyclerView>(R.id.user_list_recycler_view)
        list.layoutManager = llm
        list.adapter = usersRecyclerViewAdapter

        loadFriendList()
        loadSentRequests()
    }

    private fun setupPendingRecyclerView() {
        val llm = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        pendingRecyclerViewAdapter =
            UsersRecyclerViewAdapter(UsersRecyclerViewAdapter.ListType.PENDING, requireActivity())
        val list = binding.root.findViewById<RecyclerView>(R.id.pending_list_recycler_view)
        list.layoutManager = llm
        list.adapter = pendingRecyclerViewAdapter

        loadPendingRequests()
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
        Log.d("FRIEND_LIST", "Successfully queried friendlist: $responseBody Status code: $statusCode")
        val filteredUserList = userList.filter { !responseBody.contains(it.id) && it.id != userId }
        usersRecyclerViewAdapter.addAll(filteredUserList)
    }

    private fun loadPendingRequests() {
        RetrofitInstance.friendService.getPendingRequests().enqueue(object : Callback<List<FriendshipResponse>> {
            override fun onResponse(call: Call<List<FriendshipResponse>>, response: Response<List<FriendshipResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    getPendingRequestsSuccess(response.code(), response.body()!!)
                } else {
                    generalError(response.code(), Exception("Error loading pending requests: ${response.message()}"))
                }
            }
            override fun onFailure(call: Call<List<FriendshipResponse>>, t: Throwable) {
                generalError(500, t)
            }
        })
    }

    private fun getPendingRequestsSuccess(
        statusCode: Int,
        friendships: List<FriendshipResponse>
    ) {

        val comingIds = friendships
            .filter { it.user2Id == userId }
            .map    { it.user1Id }

        comingIds.forEach { uid ->
            userList.find { it.id == uid }?.let { user ->
                pendingRecyclerViewAdapter.add(user)
                usersRecyclerViewAdapter.remove(user)
            }
        }

        if (comingIds.isNotEmpty()) {
            binding.friendRequests.visibility = View.VISIBLE
            binding.pendingListRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun loadSentRequests() {
        RetrofitInstance.friendService.getSentRequests().enqueue(object : Callback<List<FriendshipResponse>> {
            override fun onResponse(call: Call<List<FriendshipResponse>>, response: Response<List<FriendshipResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    getSentRequestsSuccess(response.code(), response.body()!!)
                } else {
                    generalError(response.code(), Exception("Error loading sent requests: ${response.message()}"))
                }
            }
            override fun onFailure(call: Call<List<FriendshipResponse>>, t: Throwable) {
                generalError(500, t)
            }
        })
    }

    private fun getSentRequestsSuccess(
        statusCode: Int,
        sentRequests: List<FriendshipResponse>
    ) {
        val comingIds = sentRequests
            .filter { it.user1Id == userId }
            .map    { it.user2Id }

        comingIds.forEach { uid ->
            userList.find { it.id == uid }?.let { user ->
                usersRecyclerViewAdapter.addSentRequest(user.id)
            }
        }
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call!")
        e.printStackTrace()
    }

    companion object {
        @JvmStatic
        fun newInstance(userList: List<UserResponse>) = UsersFragment(userList)
    }
}
