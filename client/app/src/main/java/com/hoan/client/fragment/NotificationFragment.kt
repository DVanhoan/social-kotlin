package com.hoan.client.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoan.client.R
import com.hoan.client.adapter.NotificationAdapter
import com.hoan.client.databinding.FragmentNotificationBinding
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.viewmodel.NotificationViewModel

class NotificationFragment : Fragment(R.layout.fragment_notification) {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"

    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity()
            .getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        setupRecycler()
        observeViewModel()

        return binding.root
    }

    private fun setupRecycler() {
        notificationAdapter = NotificationAdapter(emptyList()) { notif ->
            viewModel.markAsRead(notif.id) { success ->
                if (success) viewModel.fetchNotifications()
            }
        }
        binding.notificationRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.notifications.observe(viewLifecycleOwner) { list ->
            notificationAdapter.updateList(list)
        }
        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchNotifications()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationFragment()
    }
}
