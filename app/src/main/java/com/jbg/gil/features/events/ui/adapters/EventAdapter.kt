package com.jbg.gil.features.events.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.local.db.entities.EventEntity
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.databinding.ItemEventBinding

class EventAdapter(
    private var events : List<EventEntity>,
    private val onEventClick: (EventEntity) -> Unit
) :RecyclerView.Adapter <EventViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return EventViewHolder(binding)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
       val event = events[position]

        holder.bind(event)

        holder.itemView.setOnClickListener{
            onEventClick(event)
            Log.d(Constants.GIL_TAG, "Evento: ${event.eventName}")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEvents: List<EventEntity>) {
        this.events = newEvents
        this.notifyDataSetChanged()
    }
}