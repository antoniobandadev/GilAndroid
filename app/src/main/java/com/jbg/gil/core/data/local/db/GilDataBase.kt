package com.jbg.gil.core.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jbg.gil.core.data.local.db.daos.ContactDao
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.utils.Constants

@Database(
    entities = [ContactEntity::class],
    version = 1,
    exportSchema = true
)

abstract class GilDataBase : RoomDatabase(){

    abstract fun contactDao() : ContactDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: GilDataBase? = null

        fun getDatabase(context: Context): GilDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        GilDataBase::class.java,
                        Constants.DATABASE_NAME
                    ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }



}