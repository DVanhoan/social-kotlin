package com.hoan.client.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoan.client.CreateConversationActivity
import com.hoan.client.adapter.ConversationRecyclerViewAdapter
import com.hoan.client.databinding.FragmentChatBinding
import com.hoan.client.viewmodel.ChatViewModel

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var conversationAdapter: ConversationRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        conversationAdapter = ConversationRecyclerViewAdapter(emptyList())
        binding.recyclerViewConversations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = conversationAdapter
        }

        binding.tvCreateConversation.setOnClickListener {
            startActivity(Intent(requireContext(), CreateConversationActivity::class.java))
        }

        viewModel.conversations.observe(viewLifecycleOwner) { list ->
            conversationAdapter.updateConversations(list)
        }
        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchConversations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
}


