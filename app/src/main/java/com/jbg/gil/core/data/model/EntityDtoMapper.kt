package com.jbg.gil.core.data.model

import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.data.local.db.entities.EventEntity
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.data.remote.dtos.EventDto

object EntityDtoMapper {

     fun ContactEntity.toDto(): ContactDto {
        return ContactDto (
            contactId = this.contactId,
            userId = this.userId,
            contactEmail = this.contactEmail,
            contactName = this.contactName,
            contactStatus = this.contactStatus,
            contactType = this.contactType
        )
    }

     fun ContactDto.toEntity(): ContactEntity {
        return ContactEntity(
            contactId = this.contactId.toString(),
            userId = this.userId.toString(),
            contactEmail = this.contactEmail.toString(),
            contactName = this.contactName.toString(),
            contactStatus = this.contactStatus.toString(),
            contactType = this.contactType.toString()
        )
    }

    fun EventEntity.toDto(): EventDto {
        return EventDto (
            eventId = this.eventId,
            eventName = this.eventName,
            eventDesc = this.eventDesc,
            eventType = this.eventType,
            eventDateStart = this.eventDateStart,
            eventDateEnd = this.eventDateEnd,
            eventStreet = this.eventStreet,
            eventCity = this.eventCity,
            eventStatus = this.eventStatus,
            eventImg = this.eventImg,
            eventCreatedAt = this.eventCreatedAt,
            userId = this.userId,
            eventSync = this.eventSync,
            userIdScan = this.userIdScan
        )
    }

    fun EventDto.toEntity(): EventEntity {
        return EventEntity(
            eventId = this.eventId.toString(),
            eventName = this.eventName.toString(),
            eventDesc = this.eventDesc.toString(),
            eventType = this.eventType.toString(),
            eventDateStart = this.eventDateStart.toString(),
            eventDateEnd = this.eventDateEnd.toString(),
            eventStreet = this.eventStreet.toString(),
            eventCity = this.eventCity.toString(),
            eventStatus = this.eventStatus.toString(),
            eventImg = this.eventImg.toString(),
            eventCreatedAt = this.eventCreatedAt.toString(),
            userId = this.userId.toString(),
            eventSync = this.eventSync,
            userIdScan = this.userIdScan.toString()
        )
    }

}

