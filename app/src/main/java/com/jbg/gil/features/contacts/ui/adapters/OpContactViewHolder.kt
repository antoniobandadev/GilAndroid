package com.jbg.gil.features.contacts.ui.adapters

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jbg.gil.R
import com.jbg.gil.databinding.ItemMenuGuestsBinding
import com.jbg.gil.features.contacts.data.model.OpContactItem

@SuppressLint("ClickableViewAccessibility")
class OpContactViewHolder (
    private val binding: ItemMenuGuestsBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(option: OpContactItem) {
        binding.tvItemMenu.text = option.title
        binding.imgStart.setImageResource(option.imgStart)
        binding.imgEnd.setImageResource(option.imgEnd)
    }

    init {
        itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    itemView.animate().scaleX(1.03f).scaleY(1.03f).setDuration(100).start()
                    binding.tvItemMenu.setTextColor(ContextCompat.getColor(itemView.context, R.color.secondary))
                    binding.imgEnd.setColorFilter(ContextCompat.getColor(itemView.context, R.color.secondary))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    itemView.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    binding.tvItemMenu.setTextColor(ContextCompat.getColor(itemView.context, R.color.accent))
                    binding.imgEnd.setColorFilter(ContextCompat.getColor(itemView.context, R.color.accent))
                }
            }
            false
        }
    }


}