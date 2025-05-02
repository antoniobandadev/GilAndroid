package com.jbg.gil.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbg.gil.data.GilRepository
import com.jbg.gil.data.remote.RetrofitHelper
import com.jbg.gil.data.remote.model.UserDto
import com.jbg.gil.utils.Constants
import com.jbg.gil.utils.Utils
import com.jbg.gil.utils.Utils.nowDate
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class SignUpViewModel : ViewModel() {

    private lateinit var repository: GilRepository
    private lateinit var retrofit: Retrofit

    val name = MutableLiveData<String?>()
    val nameError = MutableLiveData<Boolean>()
    val email = MutableLiveData<String?>()
    val emailError = MutableLiveData<Boolean>()
    val emailConf = MutableLiveData<String?>()
    val emailConfError = MutableLiveData<Boolean>()
    val emailEqualsError = MutableLiveData<Boolean>()
    val password = MutableLiveData<String?>()
    val passwordError = MutableLiveData<Boolean>()
    val passwordConf = MutableLiveData<String?>()
    val passwordConfError = MutableLiveData<Boolean>()
    val passwordEqualsError = MutableLiveData<Boolean>()
    val signUpSuccess = MutableLiveData<Boolean>()


    private fun validateInputs(): Boolean {

        val nameVal = name.value.orEmpty()
        val emailVal = email.value.orEmpty()
        val emailConfVal = emailConf.value.orEmpty()
        val passwordVal = password.value.orEmpty()
        val passwordConfVal = passwordConf.value.orEmpty()

        var valid = true

        if (nameVal.isEmpty() || nameVal.length < 3) {
            nameError.value = true
            valid = false
        }

        if (emailVal.isEmpty()) {
            emailError.value = true
            valid = false
        }

        if (emailConfVal.isEmpty()) {
            emailConfError.value = true
            valid = false
        }

        if (!Utils.checkEmail(emailVal)) {
            emailError.value = true
            valid = false
        }else if (emailVal != emailConfVal) {
            emailEqualsError.value = true
            valid = false
        }

        if (passwordVal.isEmpty()) {
            passwordError.value = true
            valid = false
        }

        if (passwordConfVal.isEmpty()) {
            passwordConfError.value = true
            valid = false
        }

        if (!Utils.isPasswordSecure(passwordVal)) {
            passwordError.value = true
            valid = false
        }else if (passwordVal != passwordConfVal) {
            passwordEqualsError.value = true
            valid = false
        }

        return valid
    }

    fun signUpUser(deviceId: String) {
        if (validateInputs()) {

            val nameVal = name.value.orEmpty()
            val emailVal = email.value.orEmpty()
            val passwordVal = password.value.orEmpty()

            retrofit = RetrofitHelper().getRetrofit()
            repository = GilRepository(retrofit)
            val userDateCreated = nowDate()

            viewModelScope.launch {
                try {

                    val newUser = UserDto(
                        name = nameVal,
                        email = emailVal,
                        password = passwordVal,
                        deviceId = deviceId,
                        createdAt = userDateCreated
                    )

                    val register = repository.postRegUser(newUser)
                    Log.d(Constants.GIL_TAG, "Respuesta: $register")
                    Log.d(Constants.GIL_TAG, "Respuesta: $newUser")

                    if (register.code() == 200) {
                        signUpSuccess.value = true
                    } else {
                        Log.d(Constants.GIL_TAG, "Code: ${register.code()}")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(Constants.GIL_TAG, "Error en la conexion")
                }

            }
        }
    }


    /*private val _name = MutableLiveData<String?>()
    val name  : LiveData<String?> = _name*/

}