package com.jbg.gil.features.contacts.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.jbg.gil.R
import com.jbg.gil.core.data.remote.apis.ContactApi
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.ContactDialogBinding
import com.jbg.gil.core.data.model.EntityDtoMapper.toEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class ContactDialog (
    private val newContact : Boolean,
    private val updateUI: () -> Unit,
    private var contact: ContactDto = ContactDto(
        contactId = "",
        userId = "",
        contactName = "",
        contactEmail = "",
        contactStatus = "",
        contactType = "C"
    )

) : DialogFragment() {

    @Inject
    lateinit var contactRepository: ContactRepository
    @Inject
    lateinit var apiContactApi: ContactApi
    @Inject
    lateinit var userPreferences: UserPreferences

    private val networkStatusViewModel : NetworkStatusViewModel by activityViewModels ()

    private var _binding: ContactDialogBinding? = null
    private val binding get()= _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       super.onCreateDialog(savedInstanceState)
       _binding = ContactDialogBinding.inflate(requireActivity().layoutInflater)
        focusAndTextListener()

        networkStatusViewModel.getNetworkStatus().observe(this) { status ->
            Log.d(Constants.GIL_TAG, "$status")
        }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        //dialog.window!!.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)

        //Edit Contact
        if(!newContact){
            binding.apply {
                etContactName.setText(contact.contactName)
                etContactEmail.setText(contact.contactEmail)
                btDiaContDelete.visibility = View.VISIBLE
                btDiaContSave.text = getString(R.string.update)
            }
        }

        binding.btnCloseContact.setOnClickListener {
            binding.btnCloseContact.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            dialog.dismiss()
        }

        //Delete
        binding.btDiaContDelete.setOnClickListener {
            //deleteContact()
            Utils.showConfirmAlertDialog(
                context = requireContext(),
                title = getString(R.string.delete_contact),
                message = getString(R.string.confirm_delete_contact),
                confirmText = getString(R.string.yes),
                cancelText = getString(R.string.no),
                confirmColor = ContextCompat.getColor(requireContext(), R.color.red),
                onConfirm = {
                    lifecycleScope.launch {
                        deleteContact()
                    }
                }
            )
        }

        //Save & Edit
        binding.btDiaContSave.setOnClickListener {
            if(validateInputs()) {
                DialogUtils.showLoadingDialog(requireContext())
                //Save
                if (newContact) {
                    saveContact()
                //Update
                } else {
                    updateContact()
                }
            }
        }

        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateInputs() : Boolean{

        binding.apply {
            if (etContactName.text.toString().isBlank() && etContactEmail.text.toString().isBlank()) {
                lbContactName.error = getString(R.string.invalid_name)
                lbContactEmail.error = getString(R.string.invalid_email)
                return false
            } else if (etContactName.text.toString().isBlank() ) {
                lbContactName.error = getString(R.string.invalid_name)
                return false
            }else if (etContactEmail.text.toString().isBlank()) {
                lbContactEmail.error = getString(R.string.invalid_email)
                return false
            }else if (!Utils.checkEmail(etContactEmail.text.toString().trim())) {
                lbContactEmail.error = getString(R.string.not_valid_email)
                return false
            }else{
                return true
            }

        }

    }

    private fun focusAndTextListener() {
        binding.apply {
            Utils.setupFocusAndTextListener(etContactName, lbContactName)
            Utils.setupFocusAndTextListener(etContactEmail, lbContactEmail)
        }
    }

    private fun deleteContact(){
        DialogUtils.showLoadingDialog(requireContext())
        val isConnected = networkStatusViewModel.getNetworkStatus().value

        if (isConnected == true) {
            try {
                lifecycleScope.launch {

                    val deleteMyContact = apiContactApi.deleteContact(contact.contactId, contact.userId)

                    if (deleteMyContact.code() == 200){
                        val resultBd = async {
                            contactRepository.deleteContact(contact.contactId)
                        }
                        resultBd.await()

                        getActivityRootView()?.showSnackBar(getString(R.string.contact_delete_successful))
                        DialogUtils.dismissLoadingDialog()
                        dialog?.dismiss()
                        updateUI()
                    }else {
                        getActivityRootView()?.showSnackBarError(getString(R.string.error_delete_contact))
                        DialogUtils.dismissLoadingDialog()
                        dialog?.dismiss()
                        updateUI()
                    }


                }
            }catch (e: IOException){
                e.printStackTrace()
                Log.d(Constants.GIL_TAG, "Error al eliminar el contacto")

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Api Contact: $e")
            }

        } else {
            contact.contactStatus = "C"
            try {
                lifecycleScope.launch {

                    val result = async {
                        contactRepository.updateContact(contact.toEntity())
                    }
                    result.await()

                    getActivityRootView()?.showSnackBar(getString(R.string.contact_delete_successful))

                    DialogUtils.dismissLoadingDialog()
                    dialog?.dismiss()
                    updateUI()
                }

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Insert Contact: $e")
            }

        }

    }

    private fun saveContact(){
        val isConnected = networkStatusViewModel.getNetworkStatus().value
        Log.d(Constants.GIL_TAG, "Connected: $isConnected")

        Log.d(Constants.GIL_TAG, "Save")
        binding.apply {
            contact.contactId = UUID.randomUUID().toString()
            contact.userId = userPreferences.getUserId().toString()
            contact.contactName = etContactName.text.toString()
            contact.contactEmail = etContactEmail.text.toString()
        }

        if (isConnected == true) {
            contact.contactStatus = "A"
            try {
                lifecycleScope.launch {
                    val myNewContact = apiContactApi.newContact(contact)

                    if (myNewContact.code() == 200){
                        val resultBd = async {
                            contactRepository.insertContact(contact.toEntity())
                        }
                        resultBd.await()

                        DialogUtils.dismissLoadingDialog()
                        dialog?.dismiss()
                        updateUI()
                        getActivityRootView()?.showSnackBar(getString(R.string.contact_save_successful))

                    }else{
                        getActivityRootView()?.showSnackBarError(getString(R.string.error_new_contact))
                        DialogUtils.dismissLoadingDialog()
                        dialog?.dismiss()
                        updateUI()
                    }

                }

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Api Contact: $e")
            }

        } else {
            contact.contactStatus = "P"
            try {
                lifecycleScope.launch {

                    val result = async {
                        contactRepository.insertContact(contact.toEntity())
                    }
                    result.await()

                    getActivityRootView()?.showSnackBar(getString(R.string.contact_save_successful))

                    DialogUtils.dismissLoadingDialog()
                    dialog?.dismiss()
                    updateUI()
                }

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Insert Contact: $e")
            }

        }

    }

    private fun updateContact(){
        val isConnected = networkStatusViewModel.getNetworkStatus().value
        Log.d(Constants.GIL_TAG, "Connected: $isConnected")

        binding.apply {
            contact.contactName = etContactName.text.toString()
            contact.contactEmail = etContactEmail.text.toString()
        }

        if (isConnected == true) {

            try {
                lifecycleScope.launch {
                    if (contact.contactStatus == "A") {
                        val updateMyContact = apiContactApi.updateContact(contact.contactId, contact)

                        if (updateMyContact.code() == 200) {
                            val resultBd = async {
                                contactRepository.updateContact(contact.toEntity())
                            }
                            resultBd.await()

                            dialog?.dismiss()
                            DialogUtils.dismissLoadingDialog()
                            Log.d(Constants.GIL_TAG, "Actualizado Online")
                            getActivityRootView()?.showSnackBar(getString(R.string.contact_update_successful))

                            updateUI()

                        }else{
                            getActivityRootView()?.showSnackBarError(getString(R.string.error_update_contact))
                            DialogUtils.dismissLoadingDialog()
                            dialog?.dismiss()
                            updateUI()
                        }

                    } else {
                        contact.contactStatus = "A"

                        val updateMyContact = apiContactApi.newContact(contact)

                        if (updateMyContact.code() == 200) {

                            val resultBd = async {
                                contactRepository.updateContact(contact.toEntity())
                            }
                            resultBd.await()

                            dialog?.dismiss()
                            DialogUtils.dismissLoadingDialog()
                            Log.d(Constants.GIL_TAG, "Actualizado Online")
                            getActivityRootView()?.showSnackBar(getString(R.string.contact_update_successful))

                            updateUI()

                        }else{
                            getActivityRootView()?.showSnackBarError(getString(R.string.error_update_contact))
                            DialogUtils.dismissLoadingDialog()
                            dialog?.dismiss()
                            updateUI()
                        }
                    }

                }

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Api Contact: $e")
            }

        } else {
            contact.contactStatus = "P"
            try {
                lifecycleScope.launch {

                    val result = async {
                        contactRepository.updateContact(contact.toEntity())
                    }
                    result.await()

                    DialogUtils.dismissLoadingDialog()
                    dialog?.dismiss()

                    Log.d(Constants.GIL_TAG, "Actualizado Offline")

                    getActivityRootView()?.showSnackBar(getString(R.string.contact_update_successful))

                    updateUI()
                }

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Insert Contact: $e")
            }

        }

    }


}