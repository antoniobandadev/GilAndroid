package com.jbg.gil.core.data.remote.dtos

import com.google.gson.annotations.SerializedName

data class EventGuestDto(

    @SerializedName("eventId")
    val eventId: String? = null,

    @SerializedName("eventName")
    val eventName: String? = null,

    @SerializedName("eventDesc")
    val eventDesc: String? = null,

    @SerializedName("eventType")
    val eventType: String? = null,

    @SerializedName("eventDateStart")
    val eventDateStart: String? = null,

    @SerializedName("eventDateEnd")
    val eventDateEnd: String? = null,

    @SerializedName("eventStreet")
    val eventStreet: String? = null,

    @SerializedName("eventCity")
    val eventCity: String? = null,

    @SerializedName("guestInvName")
    val guestInvName: String? = null,

    @SerializedName("guestsQR")
    val guestsQR: String? = null

)
