package com.jbg.gil.core.data.remote.dtos

data class ContactDto(
    var contactId : String,
    var userId : String,
    var contactEmail : String,
    var contactName : String,
    var contactStatus : String,
    var contactType : String,
    var contactSol: String
)

data class BasicResponse(
    val response: String
)
