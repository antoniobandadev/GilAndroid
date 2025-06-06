package com.jbg.gil.core.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.util.Patterns
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.jbg.gil.R
import com.jbg.gil.databinding.AlertDialogNegativeBinding
import com.jbg.gil.databinding.EditNameDialogBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

    /*@SuppressLint("ClickableViewAccessibility")
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
    }*/
    //____________________________________________________________________________________________
    @SuppressLint("ClickableViewAccessibility")
     fun setupHideKeyboardOnTouch(view: View, activity: Activity) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard(activity)
                false
            }
        }

        // If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupHideKeyboardOnTouch(innerView, activity)
            }
        }
    }

    private fun hideKeyboard(activity : Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
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
    fun View.showSnackBarError(
        message: String,
        duration: Int = Snackbar.LENGTH_LONG,
        actionText: String? = ContextCompat.getString(this.context, R.string.close),
        action: (() -> Unit)? = null,
        backgroundColor: Int = R.color.red,
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
    //----------------------------------------------------------------------------------------------
    fun isConnectedNow(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

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

    fun Fragment.getActivityRootView(): View? {
        return activity?.findViewById(android.R.id.content)
    }

    //----------------------------------------------------------------------------------------------

    fun showConfirmAlertDialog(
        context: Context,
        title: String,
        message: String,
        confirmText: String = "",
        cancelText: String = "",
        confirmColor: Int = ContextCompat.getColor(context, R.color.green),
        cancelColor: Int = ContextCompat.getColor(context, R.color.greyDark_load),
        onConfirm: () -> Unit
    ) {
        val binding = AlertDialogNegativeBinding.inflate(android.view.LayoutInflater.from(context))

        binding.adTitle.text = title
        binding.adMessage.text = message
        binding.btnConfirm.text = confirmText
        binding.btnCancel.text = cancelText

        binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(confirmColor)
        binding.btnCancel.backgroundTintList = ColorStateList.valueOf(cancelColor)

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnConfirm.setOnClickListener {

            onConfirm()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
    //---------------------------------------------------------------------------------------------

    fun showConfirmEditAlertDialog(
        context: Context,
        title: String,
        name: String,
        confirmText: String = "",
        cancelText: String = "",
        confirmColor: Int = ContextCompat.getColor(context, R.color.green),
        cancelColor: Int = ContextCompat.getColor(context, R.color.greyDark_load),
        onConfirm: (name: String) -> Unit
    ) {
        val binding = EditNameDialogBinding.inflate(android.view.LayoutInflater.from(context))

        binding.etEditName.setText(name)
        binding.adTitle.text = title
        binding.btnConfirm.text = confirmText
        binding.btnCancel.text = cancelText
        binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(confirmColor)
        binding.btnCancel.backgroundTintList = ColorStateList.valueOf(cancelColor)

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            val newName = binding.etEditName.text.toString()

            if (newName.isBlank()){
                binding.lbEditName.error = ContextCompat.getString(context ,R.string.required_field)
            }else{
                onConfirm(newName)
                dialog.dismiss()
            }

        }

        setupFocusAndTextListener(binding.etEditName, binding.lbEditName)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


    //----------------------------------------------------------------------------------------------

    fun showOkAlertDialog(
        context: Context,
        title: String,
        message: String,
        confirmText: String = "",
        confirmColor: Int = ContextCompat.getColor(context, R.color.greyDark_load)
    ) {
        val binding = AlertDialogNegativeBinding.inflate(android.view.LayoutInflater.from(context))
        binding.adTitle.setTextColor(ContextCompat.getColor(context, R.color.accent))
        binding.adTitle.text = title
        binding.adMessage.text = message
        binding.btnConfirm.text = confirmText
        binding.btnCancel.visibility = View.GONE
        binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(confirmColor)

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.btnConfirm.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
    //----------------------------------------------------------------------------------------------

    fun showOkAlertDialogPositiveA(
        context: Context,
        title: String,
        message: String,
        confirmText: String = "",
        confirmColor: Int = ContextCompat.getColor(context, R.color.greyDark_load),
        onConfirm: () -> Unit
    ) {
        val binding = AlertDialogNegativeBinding.inflate(android.view.LayoutInflater.from(context))
        binding.adTitle.setTextColor(ContextCompat.getColor(context, R.color.accent))
        binding.adTitle.text = title
        binding.adMessage.text = message
        binding.btnConfirm.text = confirmText
        binding.btnCancel.visibility = View.GONE
        binding.btnConfirm.backgroundTintList = ColorStateList.valueOf(confirmColor)

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        binding.btnConfirm.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    //---------------------------------------------------------------------------------------------


    fun getEventTypes(context: Context): List<String> {
        return listOf(
            context.getString(R.string.event_category_social),
            context.getString(R.string.event_category_corporate),
            context.getString(R.string.event_category_academic),
            context.getString(R.string.event_category_other)
        )
    }

    //----------------------------------------------------------------------------------------------

    fun prepareImagePart(uri: Uri?, context: Context, imageName : String): MultipartBody.Part? {
        if (uri == null) return null

        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes() ?: return null
        val fileName = "image_${System.currentTimeMillis()}.jpg"

        val requestFile = fileBytes.toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(imageName, fileName, requestFile)
    }

    //---------------------------------------------------------------------------------------------

    fun createPartFromString(text: String): RequestBody {
        val valText = text.toRequestBody("text/plain".toMediaTypeOrNull())
        return valText
    }

    //---------------------------------------------------------------------------------------------
    fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "upload_${System.currentTimeMillis()}.jpg"
            val outputFile = File(context.filesDir, fileName)

            inputStream?.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            outputFile.absolutePath // Guarda esto en Room
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //---------------------------------------------------------------------------------------------

    fun convertDate(input: String, inputFormat: String, outputFormat: String): String? {
        return try {
            val inputFormatter = SimpleDateFormat(inputFormat, Locale.getDefault())
            val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
            val date = inputFormatter.parse(input)
            date?.let { outputFormatter.format(it) }
        } catch (e: ParseException) {
            null
        }
    }

    //-------------------------------------------------------------------------------------------

    fun isDateEarlierToday(dateStr: String, format: String): Boolean {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.isLenient = false
            val inputDate: Date = sdf.parse(dateStr) ?: return false

            // Quitar la parte de la hora para comparar solo fechas
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            val inputCal = Calendar.getInstance()
            inputCal.time = inputDate
            inputCal.set(Calendar.HOUR_OF_DAY, 0)
            inputCal.set(Calendar.MINUTE, 0)
            inputCal.set(Calendar.SECOND, 0)
            inputCal.set(Calendar.MILLISECOND, 0)

            !inputCal.before(today) // true si es hoy o en el futuro
        } catch (e: Exception) {
            false
        }
    }

    //---------------------------------------------------------------------------------------------

    fun View.applyClickAnimation() {
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        scaleX = 1f
        scaleY = 1f
        animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    //----------------------------------------------------------------------------------------------

    fun isInteger(value: String): Boolean {
        return value.toIntOrNull() != null
    }



    //---------------------------------------------------------------------------------------------
}

@SuppressLint("StaticFieldLeak")
object DialogUtils {

    private var loadingDialog: Dialog? = null
    private var loadingTextView: TextView? = null
    private var loadingImageView: ImageView? = null
    private var loadingProgress: ProgressBar? = null

    fun showLoadingDialog(context: Context) {
        if (loadingDialog?.isShowing == true) return // Evita mostrarlo varias veces

        loadingDialog = Dialog(context).apply {
            setContentView(R.layout.load_dialog)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()

            loadingTextView = findViewById(R.id.tvLoad)
            loadingImageView = findViewById(R.id.ivLoad)
            loadingProgress = findViewById(R.id.pbLoad)
        }
    }

    fun updateLoadingDialogCorrect(context: Context){
        loadingProgress?.visibility = View.GONE
        loadingImageView?.visibility = View.VISIBLE
        loadingImageView?.setImageResource(R.drawable.ic_check_circle)
        loadingTextView?.text = context.getString(R.string.valid_code_scan)
    }

    fun updateLoadingDialogInvalid(context: Context){
        loadingProgress?.visibility = View.GONE
        loadingImageView?.visibility = View.VISIBLE
        loadingImageView?.setImageResource(R.drawable.ic_cancel)
        loadingImageView?.setColorFilter(context.getColor(R.color.accent))
        loadingTextView?.text = context.getString(R.string.invalid_code_scan)
    }

    fun updateLoadingDialogScanned(context: Context){
        loadingProgress?.visibility = View.GONE
        loadingImageView?.visibility = View.VISIBLE
        loadingImageView?.setImageResource(R.drawable.ic_cancel)
        loadingImageView?.setColorFilter(context.getColor(R.color.accent))
        loadingTextView?.text = context.getString(R.string.code_scan_before)
    }

    fun isLoadingDialogVisible(): Boolean {
        return loadingDialog?.isShowing == true
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