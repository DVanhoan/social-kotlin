package com.hoan.client.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.adapter.ReactionsRecyclerViewAdapter
import com.hoan.client.databinding.FragmentReactionsBinding
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.ReactionResponse
import com.hoan.client.network.RetrofitInstance
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReactionsFragment(
    private val post: PostResponse,
    private val reactions: List<ReactionResponse>
) : Fragment(R.layout.fragment_reactions) {

    private var _binding: FragmentReactionsBinding? = null
    private val binding get() = _binding!!

    private val picasso: Picasso by lazy { Picasso.get() }

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"

    private lateinit var reactionsRecyclerViewAdapter: ReactionsRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReactionsBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
        }

        setupRecyclerView()
        getReactionsOnPost(post.id)

        return binding.root
    }

    private fun setupRecyclerView() {
        val llm = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        reactionsRecyclerViewAdapter = ReactionsRecyclerViewAdapter()
        reactionsRecyclerViewAdapter.reloadReactions(reactions)
        val list = binding.root.findViewById<RecyclerView>(R.id.comments_recycler_view)
        list.layoutManager = llm
        list.adapter = reactionsRecyclerViewAdapter
    }


    private fun getReactionsOnPost(postId: Long) {
        RetrofitInstance.reactionService.getReactionsOnPost(postId)
            .enqueue(object : Callback<List<ReactionResponse>> {
                override fun onResponse(
                    call: Call<List<ReactionResponse>>,
                    response: Response<List<ReactionResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getReactionsOnPostSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error getting reactions: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<List<ReactionResponse>>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun getReactionsOnPostSuccess(statusCode: Int, responseBody: List<ReactionResponse>) {
        Log.d("GET_REACTIONS_ON_POST", "Successfully got reactions: $responseBody Status code: $statusCode")
        if (responseBody.isNotEmpty()) {
            reactionsRecyclerViewAdapter.reloadReactions(responseBody)
        }
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call")
        e.printStackTrace()
    }

    companion object {
        @JvmStatic
        fun newInstance(post: PostResponse, reactions: List<ReactionResponse>) =
            ReactionsFragment(post, reactions)
    }
}
