package com.jbg.gil.core.data.remote.dtos

import com.google.gson.annotations.SerializedName

data class GuestDto (

    @SerializedName("guestsId")
    val guestsId: String? = null,

    @SerializedName("eventId")
    val eventId: String? = null,

    @SerializedName("guestInvName")
    val guestInvName: String? = null,

    @SerializedName("guestsType")
    val guestsType: String? = null,

    @SerializedName("contactId")
    val contactId: String? = null,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("guestsCreatedAt")
    val guestsCreatedAt: String? = null,

    @SerializedName("guestsStatus")
    val guestsStatus: String? = null,

    @SerializedName("guestsQR")
    val guestsQR: String? = null,

    @SerializedName("guestsQRStatus")
    val guestsQRStatus: String? = null,

    @SerializedName("guestsQRDateScan")
    val guestsQRDateScan: String? = null,

)