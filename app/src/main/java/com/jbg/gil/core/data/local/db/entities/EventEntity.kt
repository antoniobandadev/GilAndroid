package com.jbg.gil.core.data.local.db.entities

import android.content.Intent
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jbg.gil.core.utils.Constants

@Entity(tableName = Constants.DATABASE_EVENTS_TABLE)
data class EventEntity(

    @PrimaryKey
    @ColumnInfo(name = "eventId")
    var eventId : String,
    //contactId = UUID.randomUUID().toString()

    @ColumnInfo(name = "eventName")
    var eventName : String,

    @ColumnInfo(name = "eventDesc")
    var eventDesc : String,

    @ColumnInfo(name = "eventType")
    var eventType : String,

    @ColumnInfo(name = "eventDateStart")
    var eventDateStart : String,

    @ColumnInfo(name = "eventDateEnd")
    var eventDateEnd: String,

    @ColumnInfo(name = "eventStreet")
    var eventStreet: String,

    @ColumnInfo(name = "eventCity")
    var eventCity: String,

    @ColumnInfo(name = "eventStatus")
    var eventStatus: String,

    @ColumnInfo(name = "eventImg")
    var eventImg: String,

    @ColumnInfo(name = "eventCreatedAt")
    var eventCreatedAt: String,

    @ColumnInfo(name = "userId")
    var userId: String,

    @ColumnInfo(name = "eventSync")
    var eventSync: Int

)
