package com.jbg.gil.features.login.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbg.gil.core.data.remote.dtos.UserDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.repositories.UserRepository
import com.jbg.gil.core.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//class LogInViewModel(application: Application) : AndroidViewModel(application) {
@HiltViewModel
class LogInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private var userPreferences: UserPreferences
): ViewModel() {

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
        userPreferences.saveUserId(userId)
        userPreferences.saveUserName(userName)
        userPreferences.saveUserEmail(userEmail)
        userPreferences.saveIsLogged(true)
        Log.d(Constants.GIL_TAG, "Loggeado: $userId")
    }
    fun clearFieldErrors() {
        emailError.value =  false
        passwordError.value = false
    }

    fun setLoading() {
        showLoading.value = true
    }




}