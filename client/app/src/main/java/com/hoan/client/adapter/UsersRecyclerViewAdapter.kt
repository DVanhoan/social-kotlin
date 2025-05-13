package com.hoan.client.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.databinding.ItemUserBinding
import com.hoan.client.network.response.FriendshipResponse
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersRecyclerViewAdapter(
    private val listType: ListType,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<UsersRecyclerViewAdapter.UserViewHolder>() {

    private var userList = mutableListOf<UserResponse>()
    private val sentRequests = mutableSetOf<Long>()

    private val picasso: Picasso by lazy { Picasso.get() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = userList.size

    enum class ListType {
        USER,
        PENDING,
        FRIEND
    }

    fun addAll(users: List<UserResponse>) {
        userList.clear()
        userList.addAll(users)
        notifyDataSetChanged()
    }

    fun add(user: UserResponse) {
        userList.add(user)
        notifyItemInserted(userList.size - 1)
    }

    fun remove(user: UserResponse) {
        val index = userList.indexOf(user)
        if (index != -1) {
            userList.removeAt(index)
            notifyItemRemoved(index)
        }
        }

    fun addSentRequest(userId: Long) {
        sentRequests.add(userId)
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val user = userList[position]
            picasso.load(user.profilePicture)
                .placeholder(R.color.primaryAccent)
                .into(binding.civProfilePicture)
            binding.tvFullName.text = user.fullName
            binding.tvUsername.text = user.username

            when (listType) {
                ListType.USER -> {
                    binding.removeFriendButton.visibility = View.GONE
                    binding.confirmAddButton.visibility = View.VISIBLE


                    if (sentRequests.contains(user.id)) {
                        binding.confirmAddButton.apply {
                            text = binding.root.context.getString(R.string.pending)
                            isEnabled = false
                        }
                    } else {
                        binding.confirmAddButton.apply {
                            text = binding.root.context.getString(R.string.add)
                            isEnabled = true
                            setOnClickListener {
                                addFriend(user.id)
                            }
                        }
                    }

                    val searchBox: EditText = activity.findViewById(R.id.et_search)
                    val query = searchBox.text.toString().trim()
                    if (query.isNotEmpty() && !user.username.contains(query, ignoreCase = true)) {
                        binding.userItem.visibility = View.GONE
                        binding.userItem.layoutParams = RecyclerView.LayoutParams(0, 0)
                    } else {
                        binding.userItem.visibility = View.VISIBLE
                    }
                }
                ListType.PENDING -> {
                    binding.confirmAddButton.visibility = View.VISIBLE
                    binding.removeFriendButton.visibility = View.GONE
                    binding.confirmAddButton.apply {
                        text = binding.root.context.getString(R.string.confirm)
                        isEnabled = true
                        setOnClickListener { acceptFriendRequest(user.id) }
                    }
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

                        sentRequests.add(userId)
                        val pos = userList.indexOfFirst { it.id == userId }
                        if (pos != -1) notifyItemChanged(pos)
                        Log.d("FRIEND_LIST", "Request sent to $userId")
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

    private fun acceptFriendRequest(userId: Long) {
        RetrofitInstance.friendService.acceptFriendRequest(userId)
            .enqueue(object : Callback<FriendshipResponse> {
                override fun onResponse(
                    call: Call<FriendshipResponse>,
                    response: Response<FriendshipResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        userList.firstOrNull { it.id == userId }?.let {
                            remove(it)
                        }
                        Log.d("FRIEND_LIST", "Accepted request from $userId")
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

    private fun removeFriend(userId: Long) {
        RetrofitInstance.friendService.rejectFriend(userId)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(
                    call: Call<Boolean>,
                    response: Response<Boolean>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        userList.firstOrNull { it.id == userId }?.let {
                            remove(it)
                        }
                        Log.d("FRIEND_LIST", "Removed friend $userId")
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

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call!")
        e.printStackTrace()
    }
}
