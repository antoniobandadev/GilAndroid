package com.jbg.gil.features.contacts.ui

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.jbg.gil.databinding.ContactDialogBinding
import androidx.core.graphics.drawable.toDrawable
import androidx.room.Update
import com.jbg.gil.R
import com.jbg.gil.core.data.local.db.entities.ContactEntity
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactDialog (
    private val newContact : Boolean,
    private val updateUI: () -> Unit,
    private var contact: ContactEntity = ContactEntity(
        contactId = "",
        userId = "",
        contactName = "",
        contactEmail = "",
        contactStatus = ""
    )

) : DialogFragment() {

    @Inject lateinit var contactRepository: ContactRepository

    private var _binding: ContactDialogBinding? = null
    private val binding get()= _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       super.onCreateDialog(savedInstanceState)
       _binding = ContactDialogBinding.inflate(requireActivity().layoutInflater)
        focusAndTextListener()

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        //dialog.window!!.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)


        binding.btDiaContCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btDiaContSave.setOnClickListener {
            Log.d(Constants.GIL_TAG, "Save")
            validateInputs()
            if (newContact){



            }else{

            }

        }



        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateInputs(){

        binding.apply {
            if (etContactName.text.toString().isBlank()) {
                lbContactName.error = getString(R.string.invalid_name)
            }

            if (etContactEmail.text.toString().isBlank()) {
                lbContactEmail.error = getString(R.string.invalid_email)
            }else {
                if (!Utils.checkEmail(etContactEmail.text.toString().trim())) {
                    lbContactEmail.error = getString(R.string.not_valid_email)
                }
            }
        }

    }

    private fun focusAndTextListener() {
        binding.apply {
            Utils.setupFocusAndTextListener(etContactName, lbContactName)
            Utils.setupFocusAndTextListener(etContactEmail, lbContactEmail)
        }
    }


}