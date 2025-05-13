package com.hoan.client.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoan.client.R
import com.hoan.client.adapter.CommentsRecyclerViewAdapter
import com.hoan.client.databinding.FragmentCommentsBinding
import com.hoan.client.network.response.PostResponse
import com.hoan.client.viewmodel.CommentsViewModel

class CommentsFragment(
    private val post: PostResponse
) : Fragment(R.layout.fragment_comments) {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CommentsViewModel by viewModels()
    private lateinit var commentsRecyclerViewAdapter: CommentsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)


        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        commentsRecyclerViewAdapter = CommentsRecyclerViewAdapter()
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentsRecyclerViewAdapter
        }


        viewModel.comments.observe(viewLifecycleOwner, Observer { list ->
            commentsRecyclerViewAdapter.reloadComments(list)
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { err ->
            err?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e("CommentsFragment", it)
            }
        })


        binding.sendCommentButton.setOnClickListener {
            val txt = binding.etAddComment.text.toString().trim()
            if (txt.isNotEmpty()) {
                viewModel.postComment(post.id, txt)
                binding.etAddComment.setText("")
                binding.etAddComment.clearFocus()
            }
        }

        viewModel.loadComments(post.id)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(post: PostResponse) = CommentsFragment(post)
    }
}
