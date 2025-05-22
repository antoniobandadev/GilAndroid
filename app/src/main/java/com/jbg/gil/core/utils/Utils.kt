package com.jbg.gil.core.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.provider.Settings
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.jbg.gil.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern


object Utils {
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

    fun clearError(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
    }
    //------------------------------------------------------------------------------------------

    fun checkEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    //-----------------------------------------------------------------------------------------
    fun isPasswordSecure(password: String): Boolean {
        val passwordPattern = Regex(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$"
        )
        return passwordPattern.matches(password)
    }

    //-----------------------------------------------------------------------------------------

    @SuppressLint("HardwareIds")
    fun userDevice(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    //-----------------------------------------------------------------------------------------

    fun nowDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(Calendar.getInstance().time)
    }

    //_______________________________________________________________________________________

    @SuppressLint("ClickableViewAccessibility")
    fun setupHideKeyboardOnTouch(view: View, activity: Activity) {
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val focusedView = activity.currentFocus
                if (focusedView is EditText) {
                    val outRect = Rect()
                    focusedView.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        focusedView.clearFocus()
                        val imm =
                            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                    }
                }
            }
            false
        }
    }

    //--------------------------------------------------------------------------------------------
    fun View.showSnackBar(
        message: String,
        duration: Int = Snackbar.LENGTH_LONG,
        actionText: String? = null,
        action: (() -> Unit)? = null,
        backgroundColor: Int = R.color.green,
        textColor: Int = R.color.accent,
        actionTextColor: Int = R.color.accent
    ) {
        val snackBar = Snackbar.make(this, message, duration)
        snackBar.setBackgroundTint(ContextCompat.getColor(this.context, backgroundColor))
        snackBar.setTextColor(ContextCompat.getColor(this.context, textColor))
        snackBar.setActionTextColor(ContextCompat.getColor(this.context, actionTextColor))
        snackBar.setTextMaxLines(5)
        val textView =
            snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.textSize = 11f

        actionText?.let {
            snackBar.setAction(it) {
                action?.invoke() // Ejecuta la acción proporcionada
                snackBar.dismiss() // Cierra el Snackbar al presionar la acción
            }
            //snackBar.setActionTextColor(actionTextColor)
        }

        /*if (actionText != null && action != null) {
            snackBar.setAction(actionText) { action() }
        }*/
        snackBar.show()
    }
    //---------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------

    fun showTermsDialog(context: Context) {
        val dialog = AlertDialog.Builder(context, R.style.CustomDialogTerms)
        val webView = WebView(context)
        val locale = Locale.getDefault().language
        val fileName = when (locale) {
            "es" -> "terms_es.html"
            "en" -> "terms_en.html"
            else -> "terms_en.html"
        }

        webView.loadUrl("file:///android_asset/$fileName")

        dialog.setTitle(R.string.terms_and_conditions)
            .setView(webView)
            .setPositiveButton(R.string.close, null)
            .setCancelable(false)
            .show()
    }
    //----------------------------------------------------------------------------------------------

}

object DialogUtils {

    private var loadingDialog: Dialog? = null

    fun showLoadingDialog(context: Context) {
        if (loadingDialog?.isShowing == true) return // Evita mostrarlo varias veces

        loadingDialog = Dialog(context).apply {
            setContentView(R.layout.load_dialog)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    fun dismissLoadingDialog() {
        loadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
            loadingDialog = null
        }
    }
}