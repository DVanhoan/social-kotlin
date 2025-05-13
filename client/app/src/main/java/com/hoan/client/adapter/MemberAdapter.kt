package com.hoan.client.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.network.response.UserResponse

class MemberAdapter(
    private val members: List<UserResponse>,
    private val selected: MutableSet<Long>
) : RecyclerView.Adapter<MemberAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val cb: CheckBox = v.findViewById(R.id.checkbox)
        fun bind(user: UserResponse) {
            cb.text = user.fullName ?: user.username
            cb.isChecked = selected.contains(user.id)
            cb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selected.add(user.id)
                else selected.remove(user.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false))

    override fun getItemCount() = members.size
    override fun onBindViewHolder(holder: VH, pos: Int) = holder.bind(members[pos])
}
