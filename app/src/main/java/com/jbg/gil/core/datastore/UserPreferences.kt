package com.jbg.gil.core.datastore

data class UserPreferences(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val isLogged: Boolean,
    val contactTable: Boolean
)
