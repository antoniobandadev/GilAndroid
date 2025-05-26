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
class SolDialog (
    private val received : Boolean,
    private val updateUI: () -> Unit,
    private val friendSol : ContactDto = ContactDto(
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

    private val networkStatusViewModel : NetworkStatusViewModel by activityViewModels ()

    private var _binding: SolFriendDialogBinding? = null
    private val binding get()= _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        _binding = SolFriendDialogBinding.inflate(requireActivity().layoutInflater)


        networkStatusViewModel.getNetworkStatus().observe(this) { status ->
            Log.d(Constants.GIL_TAG, "$status")
        }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.setCanceledOnTouchOutside(false)

        if(received){
            binding.apply {
                btDiaFriendDelete.visibility = View.VISIBLE
                lbFriendName.visibility = View.VISIBLE
                etFriendName.isEnabled = false
                etFriendEmail.isEnabled = false
                etFriendName.setText(friendSol.contactName)
                etFriendEmail.setText(friendSol.contactEmail)
                btDiaFriendDelete.text = getString(R.string.reject)
                btDiaFriendAdd.text = getString(R.string.accept)
            }

        }else {//Sent
            binding.apply {
                btDiaFriendAdd.visibility = View.GONE
                btDiaFriendDelete.visibility = View.VISIBLE
                lbFriendName.visibility = View.VISIBLE
                etFriendName.isEnabled = false
                etFriendEmail.isEnabled = false
                etFriendName.setText(friendSol.contactName)
                etFriendEmail.setText(friendSol.contactEmail)
                btDiaFriendDelete.text = getString(R.string.cancel_request)
            }
        }

        binding.btnCloseFriend.setOnClickListener {
            binding.btnCloseFriend.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            dialog.dismiss()
        }

        //Accept
        binding.btDiaFriendAdd.setOnClickListener {
            acceptFriend()
        }

        //Reject
        binding.btDiaFriendDelete.setOnClickListener {
            if (received) {
                Utils.showConfirmAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.reject_friend),
                    message = getString(R.string.confirm_reject_friend),
                    confirmText = getString(R.string.yes),
                    cancelText = getString(R.string.no),
                    confirmColor = ContextCompat.getColor(requireContext(), R.color.red),
                    onConfirm = {
                        lifecycleScope.launch {
                            rejectFriend()
                        }
                    }
                )
            }else{
                Utils.showConfirmAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.cancel_request),
                    message = getString(R.string.confirm_cancel_request),
                    confirmText = getString(R.string.yes),
                    cancelText = getString(R.string.no),
                    confirmColor = ContextCompat.getColor(requireContext(), R.color.red),
                    onConfirm = {
                        lifecycleScope.launch {
                            cancelFriend()
                        }
                    }
                )
            }

        }

        return dialog
    }

    private fun acceptFriend(){
        val respSolFriend = RespFriendDto(
            userId = friendSol.userId,
            friendId = friendSol.contactId,
            friendStatus = "A"
        )
        DialogUtils.showLoadingDialog(requireContext())
        val isConnected = networkStatusViewModel.getNetworkStatus().value

        if (isConnected == true) {
            try {
                lifecycleScope.launch {
                    val acceptFriend = apiContactApi.responseSol(respSolFriend)

                    if (acceptFriend.code() == 200){
                        getActivityRootView()?.showSnackBar(getString(R.string.friend_request_accepted_success))
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

    private fun rejectFriend(){
        val respSolFriend = RespFriendDto(
            userId = friendSol.userId,
            friendId = friendSol.contactId,
            friendStatus = "C"
        )
        DialogUtils.showLoadingDialog(requireContext())
        val isConnected = networkStatusViewModel.getNetworkStatus().value

        if (isConnected == true) {
            try {
                lifecycleScope.launch {
                    val rejectFriend = apiContactApi.responseSol(respSolFriend)

                    if (rejectFriend.code() == 200){
                        getActivityRootView()?.showSnackBar(getString(R.string.friend_request_rejected_success))
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

    private fun cancelFriend(){
        val respSolFriend = RespFriendDto(
            userId = friendSol.userId,
            friendId = friendSol.contactId,
            friendStatus = "C"
        )
        DialogUtils.showLoadingDialog(requireContext())
        val isConnected = networkStatusViewModel.getNetworkStatus().value

        if (isConnected == true) {
            try {
                lifecycleScope.launch {
                    val rejectFriend = apiContactApi.responseSol(respSolFriend)

                    if (rejectFriend.code() == 200){
                        getActivityRootView()?.showSnackBar(getString(R.string.friend_request_cancelled_success))
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
                Log.d(Constants.GIL_TAG, "Error al cancelar la solicitud")

            } catch (e: Exception) {
                Log.d(Constants.GIL_TAG, "Error Api Contact: $e")
            }

        } else {
            DialogUtils.dismissLoadingDialog()
            dialog?.dismiss()
            getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}