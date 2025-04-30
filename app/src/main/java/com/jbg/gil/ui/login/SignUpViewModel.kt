package com.jbg.gil.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel(){

    val nameError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()

     fun validateInputs(name: String,email: String, emailconf: String): Boolean {
        var valid = true
        nameError.value = if (name.isBlank()) {
            valid = false
            "El nombre no puede estar vacío"
        } else null

        emailError.value = if (email != emailconf) {
            valid = false
            "value"
        } else null

        return valid
    }

   /* private fun validateInputSignUp(
        name: String,
        email: String,
        emailconf: String,
        pass: String,
        passconf: String
    ): Boolean {
        var isValid = true

        if (name.isEmpty() || name.length < 3) {
            binding.lbSignName.error = getString(R.string.invalid_name)
            isValid = false
        }

        if (email != emailconf) {
            binding.lbSignEmail.error = getString(R.string.not_same_value)
            binding.lbSignEmailConf.error = getString(R.string.not_same_value)
            isValid = false
        } else {
            binding.lbSignEmail.error = null
            binding.lbSignEmailConf.error = null
            binding.lbSignEmail.isErrorEnabled = false
            binding.lbSignEmailConf.isErrorEnabled = false
        }

        if (!UIUtils.checkEmail(email)) {
            binding.lbSignEmail.error = getString(R.string.not_valid_email)
            isValid = false
        }

        if (!UIUtils.checkEmail(emailconf)) {
            binding.lbSignEmailConf.error = getString(R.string.not_valid_email)
            isValid = false
        }

        if (email.isEmpty()) {
            binding.lbSignEmail.error = getString(R.string.invalid_email)
            isValid = false
        }

        if (emailconf.isEmpty()) {
            binding.lbSignEmailConf.error = getString(R.string.invalid_confirmEmail)
            isValid = false
        }

        if (pass != passconf) {
            binding.lbSignPass.error = getString(R.string.not_same_value)
            binding.lbSignPassConf.error = getString(R.string.not_same_value)
            isValid = false
        } else {
            binding.lbSignPass.error = null
            binding.lbSignPassConf.error = null
            binding.lbSignPass.isErrorEnabled = false
            binding.lbSignPassConf.isErrorEnabled = false
        }

        if (pass.isEmpty() || pass.length < 8) {
            binding.lbSignPass.error = getString(R.string.invalid_password)
            isValid = false
        }

        if (passconf.isEmpty() || pass.length < 8) {
            binding.lbSignPassConf.error = getString(R.string.invalid_confirmPass)
            isValid = false
        }

        return isValid
    }*/




}