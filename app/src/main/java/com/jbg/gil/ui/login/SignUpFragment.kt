package com.jbg.gil.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jbg.gil.R
import com.jbg.gil.databinding.FragmentSignupBinding
import com.jbg.gil.utils.UIUtils


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusAndTextListener()

        if (validateInputLogin(
                binding.etSignName.text.toString().trim(),
                binding.etSignEmail.text.toString().trim(),
                binding.etSignEmailConf.text.toString().trim(),
                binding.etSignKey.text.toString().trim(),
                binding.etSignUser.text.toString().trim(),
                binding.etSignPass.text.toString().trim(),
                binding.etSignPassConf.text.toString().trim()
            )
        ) {
            Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
        }


    }


    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }


    private fun validateInputLogin(
        name: String,
        email: String,
        emailconf: String,
        serialKey: String,
        user: String,
        pass: String,
        passconf: String
    ): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.lbSignName.error = getString(R.string.invalid_name)
            isValid = false
        }

        if (email.isEmpty()) {
            binding.lbSignEmail.error = getString(R.string.invalid_email)
            isValid = false
        }

        if (emailconf.isEmpty()) {
            binding.lbSignEmailConf.error = getString(R.string.invalid_confirmEmail)
            isValid = false
        }

        if (email !== emailconf){
            binding.lbSignEmail.error = getString(R.string.not_same_value)
            binding.lbSignEmailConf.error = getString(R.string.not_same_value)
            isValid = false
        }

        if (serialKey.isEmpty()) {
            binding.lbSignKey.error = getString(R.string.invalid_serialKey)
            isValid = false
        }

        if (user.isEmpty()) {
            binding.lbSignUser.error = getString(R.string.invalid_username)
            isValid = false
        }

        if (pass.isEmpty()) {
            binding.lbSignPass.error = getString(R.string.invalid_password)
            isValid = false
        }

        if (passconf.isEmpty()) {
            binding.lbSignPassConf.error = getString(R.string.invalid_confirmPass)
            isValid = false
        }

        if (pass !== passconf){
            binding.lbSignPass.error = getString(R.string.not_same_value)
            binding.lbSignPassConf.error = getString(R.string.not_same_value)
            isValid = false
        }


        return isValid
    }


    private fun focusAndTextListener() {
        UIUtils.setupFocusAndTextListener(binding.etSignName, binding.lbSignName)
        UIUtils.setupFocusAndTextListener(binding.etSignEmail, binding.lbSignEmail)
        UIUtils.setupFocusAndTextListener(binding.etSignEmailConf, binding.lbSignEmailConf)
        UIUtils.setupFocusAndTextListener(binding.etSignKey, binding.lbSignKey)
        UIUtils.setupFocusAndTextListener(binding.etSignUser, binding.lbSignUser)
        UIUtils.setupFocusAndTextListener(binding.etSignPass, binding.lbSignPass)
        UIUtils.setupFocusAndTextListener(binding.etSignPassConf, binding.lbSignPassConf)
    }


}