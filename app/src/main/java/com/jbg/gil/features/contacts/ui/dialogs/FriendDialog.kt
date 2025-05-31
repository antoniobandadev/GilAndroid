package com.jbg.gil.features.contacts.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
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
import com.jbg.gil.core.data.remote.dtos.AddFriendDto
import com.jbg.gil.core.data.remote.dtos.ContactDto
import com.jbg.gil.core.data.remote.dtos.RespFriendDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.SolFriendDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class FriendDialog (
    private val new : Boolean,
    private val updateUI: () -> Unit,
    private val myFriend : ContactDto = ContactDto(
        contactId = "",
        userId = "",
        contactName = "",
        contactEmail = "",
        contactStatus = "",
        contactType = ""
    )
): DialogFragment() {

    @Inject
    lateinit var contactRepository: ContactRepository
    @Inject
    lateinit var apiContactApi: ContactApi
    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var friend : AddFriendDto

    private val networkStatusViewModel : NetworkStatusViewModel by activityViewModels ()

    private var _binding: SolFriendDialogBinding? = null
    private val binding get()= _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        _binding = SolFriendDialogBinding.inflate(requireActivity().layoutInflater)
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


        binding.btnCloseFriend.setOnClickListener {
            binding.btnCloseFriend.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            dialog.dismiss()
        }

        if (!new){
            binding.apply {
                btDiaFriendAdd.visibility = View.GONE
                btDiaFriendDelete.visibility = View.VISIBLE
                lbFriendName.visibility = View.VISIBLE

                etFriendName.setText(myFriend.contactName)
                etFriendEmail.setText(myFriend.contactEmail)
                etFriendName.isEnabled = false
                etFriendEmail.isEnabled = false
                tvTitle.text = getString(R.string.delete_friend)
            }
        }


        //Add
        binding.btDiaFriendAdd.setOnClickListener {
            if(validateInputs()) {
                DialogUtils.showLoadingDialog(requireContext())
                addFriend()
            }
        }

        binding.btDiaFriendDelete.setOnClickListener {
            Utils.showConfirmAlertDialog(
                context = requireContext(),
                title = getString(R.string.delete_friend),
                message = getString(R.string.confirm_delete_friend),
                confirmText = getString(R.string.yes),
                cancelText = getString(R.string.no),
                confirmColor = ContextCompat.getColor(requireContext(), R.color.red),
                onConfirm = {
                    lifecycleScope.launch {
                        deleteFriend()
                    }
                }
            )
        }



        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateInputs() : Boolean{

        binding.apply {
            if (etFriendEmail.text.toString().isBlank()) {
                lbFriendEmail.error = getString(R.string.invalid_email)
                return false
            }else if (!Utils.checkEmail(etFriendEmail.text.toString().trim())) {
                lbFriendEmail.error = getString(R.string.not_valid_email)
                return false
            }else{
                return true
            }

        }

    }


    private fun addFriend(){
        val isConnected = networkStatusViewModel.getNetworkStatus().value
        Log.d(Constants.GIL_TAG, "Connected: $isConnected")
        friend = AddFriendDto(
            userId = userPreferences.getUserId().toString(),
            friendEmail = binding.etFriendEmail.text.toString()
        )
        Log.d(Constants.GIL_TAG, "Add")

        if (isConnected == true) {
            try {
                lifecycleScope.launch {
                    val myNewFriend = apiContactApi.addFriend(friend)

                    if (myNewFriend.code() == 200) {
                        val body = myNewFriend.body()

                        if (body?.response == "pending"){
                            DialogUtils.dismissLoadingDialog()
                            binding.lbFriendEmail.setErrorTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.yellow)))
                            binding.lbFriendEmail.error = getString(R.string.sol_friend_pending)
                        }else{
                            DialogUtils.dismissLoadingDialog()
                            dialog?.dismiss()
                            getActivityRootView()?.showSnackBar(getString(R.string.sol_friend_send))
                        }

                    }else if (myNewFriend.code() == 404){
                        DialogUtils.dismissLoadingDialog()
                        binding.lbFriendEmail.error = getString(R.string.sol_no_friend_found)
                    }else{
                        getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                        DialogUtils.dismissLoadingDialog()
                        dialog?.dismiss()
                    }

                }

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Api Contact: $e")
            }

        } else {
            DialogUtils.dismissLoadingDialog()
            dialog?.dismiss()
            getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
        }

    }

    private fun deleteFriend(){

        val respSolFriend = RespFriendDto(
            userId = myFriend.userId.toString(),
            friendId = myFriend.contactId.toString(),
            friendStatus = "C"
        )
        DialogUtils.showLoadingDialog(requireContext())
        val isConnected = networkStatusViewModel.getNetworkStatus().value

        if (isConnected == true) {
            try {
                lifecycleScope.launch {
                    val rejectFriend = apiContactApi.responseSol(respSolFriend)

                    if (rejectFriend.code() == 200){
                        getActivityRootView()?.showSnackBar(getString(R.string.delete_friend_success))
                        DialogUtils.dismissLoadingDialog()
                        dialog?.dismiss()
                        updateUI()
                    }else {
                        getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                        DialogUtils.dismissLoadingDialog()
                        dialog?.dismiss()
                        updateUI()
                    }

                }
            }catch (e: IOException){
                e.printStackTrace()
                Log.d(Constants.GIL_TAG, "Error al actualizar la solicitud")

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Api Contact: $e")
            }

        } else {
            DialogUtils.dismissLoadingDialog()
            dialog?.dismiss()
            getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
        }

    }


    private fun focusAndTextListener() {
        binding.apply {
            Utils.setupFocusAndTextListener(etFriendEmail, lbFriendEmail)
        }
    }

}