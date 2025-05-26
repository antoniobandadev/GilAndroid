package com.jbg.gil.features.login.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jbg.gil.R
import com.jbg.gil.core.data.remote.dtos.PassUserDto
import com.jbg.gil.core.data.remote.dtos.SolPassDto
import com.jbg.gil.core.data.remote.dtos.UserDto
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.UserRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment (): Fragment() {

    private var _binding : FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val networkViewModel: NetworkStatusViewModel by viewModels()
    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var userResp : PassUserDto

    private var codeConfirmSend :Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusAndTextListener()
        Utils.setupHideKeyboardOnTouch(view, requireActivity())
        binding.btCloseFP.setOnClickListener {
            findNavController().navigateUp()
        }

        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            lifecycleScope.launch {

                binding.btForgPass.setOnClickListener {
                    if (isConnected) {
                        if (validateEmail()) {
                            if (!codeConfirmSend) {
                                sendCode()
                            } else {
                                if (validatePasswords()) {
                                    updatePassword()
                                }
                            }
                        }
                    }else{
                        getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
                    }
                }
            }
        }


        binding.etForgCode.addTextChangedListener { codeText ->
            val code = userResp.userPassCode
            if(code == codeText.toString()){
                binding.apply {
                    Log.d(Constants.GIL_TAG, "Iguales")
                    ivCode.visibility = View.VISIBLE
                    lbForgPass.visibility = View.VISIBLE
                    lbForgPassConf.visibility = View.VISIBLE
                    etForgCode.isEnabled = false
                    lbForgCode.isEndIconVisible = false
                    binding.btForgPass.isEnabled = true
                    binding.btForgPass.alpha = 1f
                }
            }else{
                if (codeText.toString().length > 5)
                    binding.lbForgCode.error = getString(R.string.incorrect_code)
            }
        }
    }

    private fun validateEmail() : Boolean{
        val emailVal = binding.etForgEmail.text.toString()
        if (emailVal.isBlank()){
            binding.lbForgEmail.error = getString(R.string.invalid_email)
            return false
        }else if(!Utils.checkEmail(emailVal)){
            binding.lbForgEmail.error = getString(R.string.not_valid_email)
            return false
        }else{
            return true
        }
    }

    private fun sendCode() {
        DialogUtils.showLoadingDialog(requireContext())
        val isConnected = networkViewModel.getNetworkStatus().value

        if (isConnected == true){

            lifecycleScope.launch {
                val locale = Locale.getDefault().language
                val userEmail = SolPassDto(binding.etForgEmail.text.toString(), locale)
                val sendCode = userRepository.postForgotPass(userEmail)
                if (sendCode.isSuccessful) {
                    userResp = sendCode.body()!!
                    Log.d(Constants.GIL_TAG, userResp.userPassCode)
                    binding.ivEmail.visibility = View.VISIBLE
                    binding.lbForgCode.visibility = View.VISIBLE
                    binding.etForgEmail.isEnabled = false
                    binding.lbForgEmail.isEndIconVisible = false
                    binding.btForgPass.isEnabled = false
                    binding.btForgPass.alpha = .5f

                    DialogUtils.dismissLoadingDialog()
                    codeConfirmSend = true
                    Utils.showOkAlertDialog(
                        context = requireContext(),
                        title = getString(R.string.send_code),
                        message = getString(R.string.verification_code_sent),
                        confirmText = getString(R.string.ok)
                    )

                }else if(sendCode.code() == 401){
                    DialogUtils.dismissLoadingDialog()
                    binding.lbForgEmail.error = getString(R.string.no_email_found)
                }else{
                    DialogUtils.dismissLoadingDialog()
                    getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                }

            }

        }else{
            DialogUtils.dismissLoadingDialog()
            getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
        }

    }


    private fun validatePasswords() : Boolean{
        val password = binding.etForgPass.text.toString()
        val passwordConfirm  =binding.etForgPassConf.text.toString()

        if (password.isBlank() || !Utils.isPasswordSecure(password)){
            binding.lbForgPass.error = getString(R.string.invalid_password)
            return false
        }else{
            if (password == passwordConfirm){
                return true
            }else{
                binding.lbForgPass.error = getString(R.string.not_same_value)
                binding.lbForgPassConf.error = getString(R.string.not_same_value)
                return false
            }
        }
    }


    private fun updatePassword() {
        DialogUtils.showLoadingDialog(requireContext())
        val isConnected = networkViewModel.getNetworkStatus().value

        if (isConnected == true) {

            lifecycleScope.launch {
                val newPass = binding.etForgPass.text.toString()
                val userData = UserDto(
                    userId = userResp.userId,
                    password = newPass
                )

                val updatePass = userRepository.putUpdatePass(userData)

                if (updatePass.isSuccessful){
                    DialogUtils.dismissLoadingDialog()
                    getActivityRootView()?.showSnackBar(getString(R.string.password_updated_success))
                    findNavController().navigateUp()
                }else{
                    getActivityRootView()?.showSnackBar(getString(R.string.server_error))
                }

            }
        }else{
            getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
        }
    }


    private fun focusAndTextListener() {
        Utils.setupFocusAndTextListener(binding.etForgEmail, binding.lbForgEmail)
        Utils.setupFocusAndTextListener(binding.etForgCode, binding.lbForgCode)
        Utils.setupFocusAndTextListener(binding.etForgPass, binding.lbForgPass)
        Utils.setupFocusAndTextListener(binding.etForgPassConf, binding.lbForgPassConf)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}