package com.jbg.gil.features.contacts.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jbg.gil.databinding.ItemMenuGuestsBinding
import com.jbg.gil.features.contacts.data.model.OpContactItem

class OpContactAdapter (
    private val options: List<OpContactItem>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<OpContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpContactViewHolder {
        val binding = ItemMenuGuestsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OpContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OpContactViewHolder, position: Int) {
        val option = options[position]
        holder.bind(option)
        holder.itemView.setOnClickListener { onClick(option.title) }
    }

    override fun getItemCount(): Int = options.size
}

