package com.jbg.gil.features.contacts.ui.adapters

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jbg.gil.R
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.databinding.ItemContactBinding

@SuppressLint("ClickableViewAccessibility")
class ContactViewHolder (
    private val binding: ItemContactBinding
): RecyclerView.ViewHolder(binding.root){

    fun bind (contact: ContactEntity){
        binding.apply {
            tvItemContact.text = contact.contactName
        }
    }

    init {
        itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    itemView.animate().scaleX(1.03f).scaleY(1.03f).setDuration(100).start()
                    binding.tvItemContact.setTextColor(ContextCompat.getColor(itemView.context, R.color.secondary))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    itemView.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    binding.tvItemContact.setTextColor(ContextCompat.getColor(itemView.context, R.color.accent))
                }
            }
            false
        }
    }

}