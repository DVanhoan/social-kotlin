package com.hoan.client.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.database.repository.CacheService
import com.hoan.client.databinding.UserItemBinding
import com.hoan.client.network.response.FriendshipResponse
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersRecyclerViewAdapter(
    private val listType: ListType,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<UsersRecyclerViewAdapter.UserViewHolder>() {

    private var userList = mutableListOf<UserResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener { Log.d("POSITION", position.toString()) }
    }

    override fun getItemCount(): Int = userList.size

    enum class ListType {
        USER,
        PENDING,
        FRIEND
    }

    fun addAll(userList: List<UserResponse>) {
        this.userList.addAll(userList)
        notifyDataSetChanged()
    }

    fun add(user: UserResponse) {
        this.userList.add(user)
        notifyItemInserted(userList.size - 1)
    }

    fun remove(user: UserResponse) {
        val index = userList.indexOf(user)
        if (index != -1) {
            userList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun removeItemAt(position: Int) {
        if (position in userList.indices) {
            userList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class UserViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val user = userList[position]
            CacheService.cacheProfilePicture(user, binding.civProfilePicture)
            binding.tvFullName.text = user.fullName
            binding.tvUsername.text = "@${user.username}"

            when (listType) {
                ListType.USER -> {
                    binding.confirmAddButton.visibility = View.VISIBLE
                    binding.removeFriendButton.visibility = View.GONE
                    binding.confirmAddButton.text =
                        binding.root.context.getString(R.string.add)
                    binding.confirmAddButton.setOnClickListener { addFriend(user.id) }
                    val searchBox: EditText =
                        activity.findViewById(R.id.et_search)
                    if (searchBox.text.isNotEmpty() && !user.username.contains(searchBox.text)) {
                        binding.userItem.visibility = View.GONE
                        binding.userItem.layoutParams =
                            RecyclerView.LayoutParams(0, 0)
                    } else {
                        binding.userItem.visibility = View.VISIBLE
                    }
                }
                ListType.PENDING -> {
                    binding.confirmAddButton.visibility = View.VISIBLE
                    binding.removeFriendButton.visibility = View.GONE
                    binding.confirmAddButton.text =
                        binding.root.context.getString(R.string.confirm)
                    binding.confirmAddButton.setOnClickListener { acceptFriendRequest(user.id) }
                    // Nếu có nút "reject" trong layout, xử lý tại đây
                }
                ListType.FRIEND -> {
                    binding.confirmAddButton.visibility = View.GONE
                    binding.removeFriendButton.visibility = View.VISIBLE
                    binding.removeFriendButton.setOnClickListener { removeFriend(user.id) }
                }
            }
        }
    }

    private fun addFriend(userId: Long) {
        RetrofitInstance.friendService.addFriend(userId)
            .enqueue(object : Callback<FriendshipResponse> {
                override fun onResponse(
                    call: Call<FriendshipResponse>,
                    response: Response<FriendshipResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        addFriendSuccess(response.code(), response.body()!!, userId)
                    } else {
                        generalError(
                            response.code(),
                            Exception("Error adding friend: ${response.message()}")
                        )
                    }
                }
                override fun onFailure(call: Call<FriendshipResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun addFriendSuccess(
        statusCode: Int,
        responseBody: FriendshipResponse,
        userId: Long
    ) {
        Log.d(
            "FRIEND_LIST",
            "Successfully sent friend request: $responseBody, code: $statusCode"
        )
        userList.firstOrNull { it.id == userId }?.let { remove(it) }
    }

    private fun acceptFriendRequest(userId: Long) {
        RetrofitInstance.friendService.acceptFriendRequest(userId)
            .enqueue(object : Callback<FriendshipResponse> {
                override fun onResponse(
                    call: Call<FriendshipResponse>,
                    response: Response<FriendshipResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        acceptFriendRequestSuccess(response.code(), response.body()!!, userId)
                    } else {
                        generalError(
                            response.code(),
                            Exception("Error accepting friend request: ${response.message()}")
                        )
                    }
                }
                override fun onFailure(call: Call<FriendshipResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun acceptFriendRequestSuccess(
        statusCode: Int,
        responseBody: FriendshipResponse,
        userId: Long
    ) {
        Log.d(
            "FRIEND_LIST",
            "Successfully accepted friend request: $responseBody, code: $statusCode"
        )
        userList.firstOrNull { it.id == userId }?.let { remove(it) }
    }

    private fun removeFriend(userId: Long) {
        RetrofitInstance.friendService.rejectFriend(userId)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(
                    call: Call<Boolean>,
                    response: Response<Boolean>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        removeFriendSuccess(response.code(), response.body()!!, userId)
                    } else {
                        generalError(
                            response.code(),
                            Exception("Error removing friend: ${response.message()}")
                        )
                    }
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun removeFriendSuccess(
        statusCode: Int,
        responseBody: Boolean,
        userId: Long
    ) {
        Log.d("FRIEND_LIST", "Successfully removed friend, code: $statusCode")
        userList.firstOrNull { it.id == userId }?.let { remove(it) }
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call!")
        e.printStackTrace()
    }
}
