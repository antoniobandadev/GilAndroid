package com.jbg.gil.core.datastore

data class UserPreferences(
    val userName: String,
    val userEmail: String,
    val isLogged: Boolean
)
