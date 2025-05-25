package com.jbg.gil.features.contacts.data.model

import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.remote.dtos.ContactDto

object ContactMapper {

     fun ContactEntity.toDto(): ContactDto {
        return ContactDto (
            contactId = this.contactId,
            userId = this.userId,
            contactEmail = this.contactEmail,
            contactName = this.contactName,
            contactStatus = this.contactStatus
        )
    }

     fun ContactDto.toEntity(): ContactEntity {
        return ContactEntity(
            contactId = this.contactId,
            userId = this.userId,
            contactEmail = this.contactEmail,
            contactName = this.contactName,
            contactStatus = this.contactStatus
        )
    }
}