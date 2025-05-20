package com.jbg.gil.core.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jbg.gil.core.utils.Constants

@Entity(tableName = Constants.DATABASE_CONTACTS_TABLE)
data class ContactEntity(

    @PrimaryKey
    @ColumnInfo(name = "contactId")
    val contactId : String,
    //contactId = UUID.randomUUID().toString()

    @ColumnInfo(name = "userId")
    val userId : String,

    @ColumnInfo(name = "contactEmail")
    val contactEmail : String,

    @ColumnInfo(name = "contactName")
    val contactName : String,

    @ColumnInfo(name = "contactStatus")
    val contactStatus : Char

)
