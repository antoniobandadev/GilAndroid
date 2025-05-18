package com.jbg.gil.features.contacts.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.jbg.gil.databinding.ItemMenuGuestsBinding
import com.jbg.gil.features.contacts.data.model.OpContactItem

class OpContactViewHolder (
    private val binding: ItemMenuGuestsBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(option: OpContactItem) {
        binding.tvItemMenu.text = option.title
        binding.imgStart.setImageResource(option.imgStart)
        binding.imgEnd.setImageResource(option.imgEnd)
    }


}