package com.jbg.gil.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jbg.gil.R
import com.jbg.gil.databinding.FragmentLoginBinding
import com.jbg.gil.ui.home.HomeActivity
import com.jbg.gil.utils.Utils
import com.jbg.gil.utils.Utils.showSnackBar


class LogInFragment : Fragment() {

    private  var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel : LogInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusAndTextListener()
        Utils.setupHideKeyboardOnTouch(view , requireActivity())

        binding.apply {
            etLogUser.doAfterTextChanged { value ->
                viewModel.email.value = value.toString()
            }
            etLogPass.doAfterTextChanged { value ->
                viewModel.password.value = value.toString()
            }

            tvRegister.setOnClickListener {
                binding.tvRegister.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.secondary
                    )
                )
                findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
            }
        }

        viewModel.apply {

            emailError.observe(viewLifecycleOwner){ error ->
                if(error){
                    binding.lbLogUser.error = getString(R.string.invalid_logEmail)
                }
            }

            passwordError.observe(viewLifecycleOwner){ error ->
                if(error) {
                    binding.lbLogPass.error = getString(R.string.invalid_logPass)
                }
            }

        }


        binding.btLogIn.setOnClickListener {
            viewModel.logIn()

            viewModel.apply {

                invalidCredentials.observe(viewLifecycleOwner){ error ->
                    if(error){
                        binding.root.showSnackBar(getString(R.string.invalid_data), backgroundColor = R.color.red, actionText = getString(R.string.close))
                    }
                }

                loginSuccess.observe(viewLifecycleOwner){ success ->
                    if (success){
                        val startIntentH = Intent(requireContext(), HomeActivity::class.java)
                        startActivity(startIntentH)
                    }
                }
            }


        }

    }

    private fun focusAndTextListener() {
        Utils.setupFocusAndTextListener(binding.etLogUser, binding.lbLogUser)
        Utils.setupFocusAndTextListener(binding.etLogPass, binding.lbLogPass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}