package com.jbg.gil.features.contacts.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.databinding.ItemContactBinding

class ContactAdapter (
    private var contacts : List<ContactEntity>,
    private val onContactClick: (ContactEntity) -> Unit
): RecyclerView.Adapter<ContactViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
       val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.bind(contact)

        holder.itemView.setOnClickListener{
            onContactClick(contact)
            Log.d(Constants.GIL_TAG, "Contacto: ${contact.contactName}")
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newContacts: List<ContactEntity>) {
        this.contacts = newContacts
        notifyDataSetChanged()
    }

}