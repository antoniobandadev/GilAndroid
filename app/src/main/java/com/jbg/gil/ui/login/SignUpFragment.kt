package com.jbg.gil.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jbg.gil.R
import com.jbg.gil.databinding.FragmentSignupBinding
import com.jbg.gil.utils.UIUtils
import com.jbg.gil.utils.UIUtils.userDevice


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

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
        UIUtils.setupHideKeyboardOnTouch(view, requireActivity())

        binding.btCloseSign.setOnClickListener{
            findNavController().navigateUp()
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

        }

        binding.apply {
            etSignName.doAfterTextChanged {
                viewModel.name.value = it.toString()
            }
            etSignEmail.doAfterTextChanged {
                viewModel.email.value = it.toString()
            }
            etSignEmailConf.doAfterTextChanged {
                viewModel.emailConf.value = it.toString()
            }
            etSignPass.doAfterTextChanged {
                viewModel.password.value = it.toString()
            }
            etSignPassConf.doAfterTextChanged {
                viewModel.passwordConf.value = it.toString()
            }
        }

        viewModel.signUpSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                // Navegar, guardar sesión, etc.
                findNavController().navigateUp()
            }
        }

        binding.btSignUp.setOnClickListener {
            val deviceId = userDevice(requireContext())
            viewModel.signUpUser(deviceId)

            /*               val snackbar : Snackbar = Snackbar.make(view,"", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(requireContext().getColor(R.color.red))

                            snackbar.setText("Registro exitoso!")
                            snackbar.setBackgroundTint(requireContext().getColor(R.color.green))
                            findNavController().navigateUp()
            */

        }
    }

    private fun focusAndTextListener() {
        UIUtils.setupFocusAndTextListener(binding.etSignName, binding.lbSignName)
        UIUtils.setupFocusAndTextListener(binding.etSignEmail, binding.lbSignEmail)
        UIUtils.setupFocusAndTextListener(binding.etSignEmail, binding.lbSignEmailConf)
        UIUtils.setupFocusAndTextListener(binding.etSignEmailConf, binding.lbSignEmail)
        UIUtils.setupFocusAndTextListener(binding.etSignEmailConf, binding.lbSignEmailConf)
        UIUtils.setupFocusAndTextListener(binding.etSignPass, binding.lbSignPass)
        UIUtils.setupFocusAndTextListener(binding.etSignPass, binding.lbSignPassConf)
        UIUtils.setupFocusAndTextListener(binding.etSignPassConf, binding.lbSignPass)
        UIUtils.setupFocusAndTextListener(binding.etSignPassConf, binding.lbSignPassConf)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}