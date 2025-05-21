package com.jbg.gil.features.contacts.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.databinding.ItemContactBinding

class ContactViewHolder (
    private val binding: ItemContactBinding
): RecyclerView.ViewHolder(binding.root){

    fun bind (contact: ContactEntity){
        binding.apply {
            tvItemContact.text = contact.contactName
        }
    }

}