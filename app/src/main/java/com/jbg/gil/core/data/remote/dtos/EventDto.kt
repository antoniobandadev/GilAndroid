package com.jbg.gil.core.data.remote.dtos

import com.google.gson.annotations.SerializedName

data class EventDto(

    @SerializedName("eventId")
    val eventId: Int? = 0,

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

    @SerializedName("eventStatus")
    val eventStatus: String? = null,

    @SerializedName("eventImg")
    val eventImg: String? = null,

    @SerializedName("userId")
    val userId: Int? = 0

)
