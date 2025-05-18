package com.jbg.gil.features.login.ui

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jbg.gil.core.model.UserDto
import com.jbg.gil.core.network.data.GilRepository
import com.jbg.gil.core.network.remote.RetrofitHelper
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.dataStore
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class LogInViewModel(application: Application) : AndroidViewModel(application) {



    private lateinit var repository: GilRepository
    private lateinit var retrofit: Retrofit
    private val dataStore = application.dataStore

    val email = MutableLiveData<String?>()
    val emailError = MutableLiveData<Boolean>()
    val password = MutableLiveData<String?>()
    val passwordError = MutableLiveData<Boolean>()
    val invalidCredentials = MutableLiveData<Boolean>()
    val loginSuccess = MutableLiveData<Boolean>()
    val showLoading = MutableLiveData<Boolean>()
    val serverError = MutableLiveData<Boolean>()

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

        if (validateInputs()) {
            setLoading()

            val emailVal = email.value.orEmpty()
            val passwordVal = password.value.orEmpty()

            retrofit = RetrofitHelper().getRetrofit()
            repository = GilRepository(retrofit)

            viewModelScope.launch {
                try {
                    val user = UserDto (
                        email = emailVal,
                        password = passwordVal
                    )

                    val login = repository.postLogUser(user)

                    if (login.code() == 200){
                        loginSuccess.value = true
                        saveLogged(emailVal)

                    }else if(login.code() == 401){
                        invalidCredentials.value = true
                        Log.d(Constants.GIL_TAG, "Respuesta: $login")
                        Log.d(Constants.GIL_TAG, "Respuesta: $user")

                    }else if(login.code() == 500){
                        serverError.value = true
                    }

                }catch (e: Exception){
                    Log.d(Constants.GIL_TAG, e.toString())
                }
            }

            /*if (emailVal == "admin" && passwordVal == "123") {
                loginSuccess.value = true
                saveLogged(emailVal)
            } else {
                invalidCredentials.value = true
            }*/

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
    fun clearFieldErrors() {
        emailError.value =  false
        passwordError.value = false
    }

    fun setLoading() {
        showLoading.value = true
    }




}