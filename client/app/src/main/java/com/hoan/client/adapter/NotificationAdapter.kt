package com.hoan.client.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.databinding.ItemNotificationBinding
import com.hoan.client.network.response.Notification
import com.google.gson.JsonObject

class NotificationAdapter(
    private var items: List<Notification>,
    private val onItemClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.VH>() {

    inner class VH(private val b: ItemNotificationBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(n: Notification) {

            val title = when {
                n.data.containsKey("snippet") -> n.data["snippet"].toString()
                n.data.containsKey("excerpt") -> n.data["excerpt"].toString()
                n.data.containsKey("message") -> n.data["message"].toString()
                else -> n.type
            }
            b.tvNotificationTitle.text = title

            b.tvNotificationContent.text = "Vào lúc ${n.created_at}"

            itemView.setOnClickListener { onItemClick(n) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    fun updateList(newList: List<Notification>) {
        items = newList
        notifyDataSetChanged()
    }
}
