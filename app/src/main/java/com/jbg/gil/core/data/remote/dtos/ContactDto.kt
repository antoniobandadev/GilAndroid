package com.jbg.gil.core.data.remote.dtos

data class ContactDto(
    val contactId : String,
    val userId : String,
    val contactEmail : String,
    val contactName : String,
    val contactStatus : String
)
