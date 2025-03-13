package com.jbg.gil.utils

import android.util.Patterns
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

object UIUtils {
    //Change status error in forms//-----------------------------------------------------------
    fun setupFocusAndTextListener(editText: EditText, textInputLayout: TextInputLayout) {
        // Listener change focus
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearError(textInputLayout)
            }
        }

        // Listener change text
        editText.addTextChangedListener {
            clearError(textInputLayout)
        }
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
    }
    //------------------------------------------------------------------------------------------

    fun checkEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}