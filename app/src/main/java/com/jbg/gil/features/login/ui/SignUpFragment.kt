package com.jbg.gil.features.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jbg.gil.R
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.databinding.FragmentSignupBinding
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.userDevice


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()
    private val networkViewModel: NetworkStatusViewModel by viewModels()

    private var isConnectedApp : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusAndTextListener()
        Utils.setupHideKeyboardOnTouch(view, requireActivity())
        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            isConnectedApp = isConnected
        }

        binding.apply {
            etSignName.doAfterTextChanged {
                viewModel.name.value = it.toString().trim()
            }
            etSignEmail.doAfterTextChanged {
                viewModel.email.value = it.toString().trim()
            }
            etSignEmailConf.doAfterTextChanged {
                viewModel.emailConf.value = it.toString().trim()
            }
            etSignPass.doAfterTextChanged {
                viewModel.password.value = it.toString().trim()
            }
            etSignPassConf.doAfterTextChanged {
                viewModel.passwordConf.value = it.toString().trim()
            }

            btCloseSign.setOnClickListener{
                findNavController().navigateUp()
            }

            btSignUp.setOnClickListener {
                if (isConnectedApp) {
                    val deviceId = userDevice(requireContext())
                    viewModel.signUpUser(deviceId)
                }else{
                // No connected
                    binding.root.showSnackBar(
                        getString(R.string.no_internet_connection),
                        backgroundColor = R.color.red,
                        actionText = getString(R.string.close)
                    )
                }
            }
        }

        viewModel.apply {
            nameError.observe(viewLifecycleOwner){ error ->
                if(error){
                    binding.lbSignName.error =  getString(R.string.invalid_name)
                }
            }
            emailError.observe(viewLifecycleOwner){ error ->
                if(error){
                    binding.lbSignEmail.error = getString(R.string.not_valid_email)
                }
            }
            emailConfError.observe(viewLifecycleOwner){ error ->
                if(error){
                    binding.lbSignEmailConf.error = getString(R.string.not_valid_email)
                }
            }
            emailEqualsError.observe(viewLifecycleOwner){ error ->
                if (error){
                    binding.lbSignEmail.error = getString(R.string.not_same_value)
                    binding.lbSignEmailConf.error = getString(R.string.not_same_value)
                }
            }
            passwordError.observe(viewLifecycleOwner){ error ->
                if(error){
                    binding.lbSignPass.error = getString(R.string.invalid_password)
                }
            }
            passwordConfError.observe(viewLifecycleOwner){ error ->
                if(error){
                    binding.lbSignPassConf.error = getString(R.string.invalid_confirmPass)
                }
            }
            passwordEqualsError.observe(viewLifecycleOwner){ error ->
                if(error){
                 binding.lbSignPass.error = getString(R.string.not_same_value)
                 binding.lbSignPassConf.error = getString(R.string.not_same_value)
                }else{
                    binding.lbSignPass.error = null
                    binding.lbSignPassConf.error = null
                }
            }

            signUpSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    binding.root.showSnackBar(getString(R.string.registration_success))

                    findNavController().navigateUp()
                }
            }

        }
    }

    private fun focusAndTextListener() {
        Utils.setupFocusAndTextListener(binding.etSignName, binding.lbSignName)
        Utils.setupFocusAndTextListener(binding.etSignEmail, binding.lbSignEmail)
        Utils.setupFocusAndTextListener(binding.etSignEmail, binding.lbSignEmailConf)
        Utils.setupFocusAndTextListener(binding.etSignEmailConf, binding.lbSignEmail)
        Utils.setupFocusAndTextListener(binding.etSignEmailConf, binding.lbSignEmailConf)
        Utils.setupFocusAndTextListener(binding.etSignPass, binding.lbSignPass)
        Utils.setupFocusAndTextListener(binding.etSignPass, binding.lbSignPassConf)
        Utils.setupFocusAndTextListener(binding.etSignPassConf, binding.lbSignPass)
        Utils.setupFocusAndTextListener(binding.etSignPassConf, binding.lbSignPassConf)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}