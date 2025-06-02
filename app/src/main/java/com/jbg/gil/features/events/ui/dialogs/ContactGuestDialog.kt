package com.jbg.gil.features.events.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.jbg.gil.R
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.data.remote.dtos.GuestDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.repositories.GuestRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.GuestsDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ContactGuestDialog (
    private val eventId : String,
    private val updateUI: () -> Unit,
    private val guestType: Int,
    private var contact: ContactDto = ContactDto(
        contactId = "",
        contactName = "",
        contactEmail = ""
    )

) : DialogFragment() {

    private var _binding: GuestsDialogBinding? = null
    private val binding get()= _binding!!

    @Inject
    lateinit var guestRepository: GuestRepository
    @Inject
    lateinit var userPreferences: UserPreferences

    private val locale = Locale.getDefault().language

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        _binding = GuestsDialogBinding.inflate(requireActivity().layoutInflater)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        focusAndTextListener()

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.setCanceledOnTouchOutside(false)

        binding.apply {
            etInviteName.setText(contact.contactName.toString())
            etContactName.setText(contact.contactName.toString())
            etContactEmail.setText(contact.contactEmail.toString())
        }


        binding.btnCloseGuest.setOnClickListener {
            binding.btnCloseGuest.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            dialog.dismiss()
        }

        binding.btnSendInvitation.setOnClickListener {
            if (Utils.isConnectedNow(requireContext())) {
                if (validateInputs()) {
                    val guestInvName = binding.etInviteName.text.toString()
                    DialogUtils.showLoadingDialog(requireContext())
                    lifecycleScope.launch {
                        val guest: GuestDto

                        if (guestType.toString() == "0") {
                            guest = GuestDto(
                                eventId = eventId,
                                guestInvName = guestInvName,
                                guestsType = guestType.toString(),
                                contactId = contact.contactId,
                                userSendId = userPreferences.getUserId().toString(),
                                userLanguage = locale
                            )
                        } else {
                            guest = GuestDto(
                                eventId = eventId,
                                guestInvName = guestInvName,
                                guestsType = guestType.toString(),
                                userId = contact.contactId,
                                userSendId = userPreferences.getUserId().toString(),
                                userLanguage = locale
                            )
                        }


                        Log.d(Constants.GIL_TAG, "Guest: $guest")
                        val insertGuest = guestRepository.insertGuest(guest)

                        if (insertGuest.isSuccessful) {
                            DialogUtils.dismissLoadingDialog()
                            dialog.dismiss()
                            getActivityRootView()?.showSnackBar(getString(R.string.send_invitation_success))
                            updateUI()
                        } else {
                            dialog.dismiss()
                            DialogUtils.dismissLoadingDialog()
                            getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                        }

                    }
                }
            }else{
                dialog.dismiss()
                DialogUtils.dismissLoadingDialog()
                getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
            }
        }

        return dialog

    }

    private fun validateInputs() : Boolean{
        if (binding.etInviteName.text.toString().isBlank()){
            binding.lbInviteName.error = getString(R.string.required_field)
            return false
        }else{
            return true
        }
    }

    private fun focusAndTextListener() {
        binding.apply {
            Utils.setupFocusAndTextListener(etInviteName, lbInviteName)
            Utils.setupFocusAndTextListener(etContactName, lbContactName)
            Utils.setupFocusAndTextListener(etContactEmail, lbContactEmail)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }




}