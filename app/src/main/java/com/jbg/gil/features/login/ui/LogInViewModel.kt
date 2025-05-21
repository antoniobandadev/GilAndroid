package com.jbg.gil.features.login.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbg.gil.core.data.remote.dtos.UserDto
import com.jbg.gil.core.repositories.UserRepository
import com.jbg.gil.core.data.remote.RetrofitHelper
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject

//class LogInViewModel(application: Application) : AndroidViewModel(application) {
@HiltViewModel
class LogInViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
): ViewModel() {

    private val dataStore = context.dataStore

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

            viewModelScope.launch {
                try {
                    val user = UserDto (
                        email = emailVal,
                        password = passwordVal
                    )

                    val login = userRepository.postLogUser(user)

                    if (login.code() == 200){
                        Log.d(Constants.GIL_TAG, "Respuesta: ${login.body()}")
                        val userLog = login.body()
                        loginSuccess.value = true
                        saveLogged(userLog?.email.toString(), userLog?.userId.toString(), userLog?.name.toString())

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

    private fun saveLogged(userEmail:String, userId : String, userName: String ) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey("userName")] = userName
                preferences[stringPreferencesKey("userEmail")] = userEmail
                preferences[stringPreferencesKey("userId")] = userId
                preferences[booleanPreferencesKey("isLogged")] = true
                Log.d(Constants.GIL_TAG, "Loggeado: ${userId}")
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