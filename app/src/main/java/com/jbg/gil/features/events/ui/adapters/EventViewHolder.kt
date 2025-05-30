package com.jbg.gil.features.events.ui.adapters

import com.jbg.gil.core.data.local.db.entities.EventEntity
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jbg.gil.R
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.databinding.ItemEventBinding
import java.util.Locale

class EventViewHolder(
    private val binding: ItemEventBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val locale = Locale.getDefault().language
    private val strDateFormat = when (locale) {
        "es" -> "dd/MM/yyyy"
        "en" -> "MM/dd/yyyy"
        else -> "MM/dd/yyyy"
    }
    private val strDateFormatBD = "yyyy-MM-dd"

    fun bind (event: EventEntity){

        val startDate = Utils.convertDate(event.eventDateStart, strDateFormatBD, strDateFormat)
        val endDate = Utils.convertDate(event.eventDateEnd, strDateFormatBD, strDateFormat)

        binding.apply {
            tvEventName.text = event.eventName
            tvEventDesc.text = event.eventDesc
            tvEventDate.text = "$startDate - ${endDate}"
        }


        if (event.eventImg != "null") {

            Glide.with(binding.root.context)
                .load(event.eventImg)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        binding.ivEvent.setImageDrawable(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        binding.ivEvent.setImageDrawable(errorDrawable)
                    }
                })
        }else{
            binding.ivEvent.setImageResource(R.drawable.ic_event_img)
        }
    }

}