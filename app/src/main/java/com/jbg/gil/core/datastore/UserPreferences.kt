package com.jbg.gil.core.datastore

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    fun saveUserName(userName: String) {
        prefs.edit { putString("userName", userName) }
    }
    fun saveUserEmail(userEmail: String) {
        prefs.edit { putString("userEmail", userEmail) }
    }
    fun saveUserId(userId: String) {
        prefs.edit { putString("userId", userId) }
    }
    fun saveIsLogged(isLogged: Boolean) {
        prefs.edit { putBoolean("isLogged", isLogged) }
    }
    fun saveContactTable(contactTable: Boolean) {
        prefs.edit { putBoolean("contactTable", contactTable) }
    }
    fun saveFriendTable(friendTable: Boolean) {
        prefs.edit { putBoolean("friendTable", friendTable) }
    }

    fun getUserName(): String? {
        return prefs.getString("userName", null)
    }

    fun getUserEmail(): String? {
        return prefs.getString("userEmail", null)
    }

    fun getUserId(): String? {
        return prefs.getString("userId", null)
    }

    fun getIsLogged(): Boolean {
        return prefs.getBoolean("isLogged", false)
    }

    fun getContactTable(): Boolean {
        return prefs.getBoolean("contactTable", false)
    }

    fun getFriendTable(): Boolean {
        return prefs.getBoolean("friendTable", false)
    }

    fun clearAll() {
        prefs.edit { clear() }
    }

}