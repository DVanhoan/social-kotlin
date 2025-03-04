package com.hoan.client.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hoan.client.R
import com.hoan.client.databinding.FragmentFriendBinding
import com.hoan.client.network.response.UserResponse

class FriendFragment(private val userList: List<UserResponse>) : Fragment(R.layout.fragment_friend) {

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)

        childFragmentManager.beginTransaction()
            .replace(R.id.friend_fragment_container, UsersFragment.newInstance(userList))
            .commit()

        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
        }

        binding.myFriendsButton.setOnClickListener {
            binding.addFriendsButton.setBackgroundResource(R.drawable.underline_dark)
            binding.myFriendsButton.setBackgroundResource(R.drawable.underline_thick)
            binding.addFriendsButton.typeface = Typeface.DEFAULT
            binding.myFriendsButton.typeface = Typeface.DEFAULT_BOLD

            childFragmentManager.beginTransaction()
                .replace(R.id.friend_fragment_container, MyFriendsFragment.newInstance(), "FRIEND_FRAGMENT")
                .commit()
        }

        binding.addFriendsButton.setOnClickListener {
            binding.myFriendsButton.setBackgroundResource(R.drawable.underline_dark)
            binding.addFriendsButton.setBackgroundResource(R.drawable.underline_thick)
            binding.myFriendsButton.typeface = Typeface.DEFAULT
            binding.addFriendsButton.typeface = Typeface.DEFAULT_BOLD

            childFragmentManager.beginTransaction()
                .replace(R.id.friend_fragment_container, UsersFragment.newInstance(userList), "FRIEND_FRAGMENT")
                .commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(userList: List<UserResponse>) = FriendFragment(userList)
    }
}
