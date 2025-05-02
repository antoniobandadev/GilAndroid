package com.jbg.gil.ui.login

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jbg.gil.utils.Constants
import com.jbg.gil.utils.dataStore
import kotlinx.coroutines.launch

class LogInViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = application.dataStore

    val email = MutableLiveData<String?>()
    val emailError = MutableLiveData<Boolean>()
    val password = MutableLiveData<String?>()
    val passwordError = MutableLiveData<Boolean>()
    val invalidCredentials = MutableLiveData<Boolean>()
    val loginSuccess = MutableLiveData<Boolean>()

    private fun validateInputs(): Boolean {

        val emailVal = email.value.orEmpty()
        val passwordVal = password.value.orEmpty()

        var isValid = true

        if (emailVal.isEmpty()) {
            emailError.value = true
            isValid = false
        }

        if (passwordVal.isEmpty()) {
            passwordError.value = true
            isValid = false
        }

        return isValid
    }

    fun logIn() {

        val emailVal = email.value.orEmpty()
        val passwordVal = password.value.orEmpty()

        if (validateInputs()) {
            if (emailVal == "admin" && passwordVal == "123") {
                loginSuccess.value = true
                saveLogged(emailVal)
            } else {
                invalidCredentials.value = true
            }

        }
    }

    private fun saveLogged(emailVal:String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey("userName")] = emailVal
                preferences[stringPreferencesKey("userEmail")] = emailVal
                preferences[booleanPreferencesKey("isLogged")] = true
                Log.d(Constants.GIL_TAG, "Loggeado")
            }
        }
        //context?.
    }

}