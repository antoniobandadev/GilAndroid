package com.jbg.gil.core.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jbg.gil.core.utils.Constants

@Entity(tableName = Constants.DATABASE_CONTACTS_TABLE)
data class ContactEntity(

    @PrimaryKey
    @ColumnInfo(name = "contactId")
    var contactId : String,
    //contactId = UUID.randomUUID().toString()

    @ColumnInfo(name = "userId")
    var userId : String,

    @ColumnInfo(name = "contactEmail")
    var contactEmail : String,

    @ColumnInfo(name = "contactName")
    var contactName : String,

    @ColumnInfo(name = "contactStatus")
    var contactStatus : String

)
