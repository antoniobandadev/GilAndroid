package com.jbg.gil.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.jbg.gil.R
import com.jbg.gil.data.GilRepository
import com.jbg.gil.data.remote.RetrofitHelper
import com.jbg.gil.data.remote.model.UserDto
import com.jbg.gil.databinding.FragmentSignupBinding
import com.jbg.gil.utils.Constants
import com.jbg.gil.utils.UIUtils
import com.jbg.gil.utils.UIUtils.nowDate
import com.jbg.gil.utils.UIUtils.userDevice
import kotlinx.coroutines.launch
import retrofit2.Retrofit


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: GilRepository
    private lateinit var retrofit: Retrofit

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

        /*viewModel.nameError.observe(viewLifecycleOwner){
            binding.lbSignName.error = it
        }
        viewModel.emailError.observe(viewLifecycleOwner){
            binding.lbSignEmail.error = it
        }*/

        binding.btSignUp.setOnClickListener {

            /*viewModel.validateInputs(binding.etSignName.text.toString().trim(),
                binding.etSignEmail.text.toString().trim(),
                binding.etSignEmailConf.text.toString().trim())*/




            if (validateInputSignUp(
                    binding.etSignName.text.toString().trim(),
                    binding.etSignEmail.text.toString().trim(),
                    binding.etSignEmailConf.text.toString().trim(),
                    binding.etSignPass.text.toString().trim(),
                    binding.etSignPassConf.text.toString().trim()

                )
            ) {
                val userName = binding.etSignName.text.toString().trim()
                val userEmail = binding.etSignEmail.text.toString().trim()
                val userPass = binding.etSignPass.text.toString().trim()
                val deviceId = userDevice(requireContext())
                val userDateCreated = nowDate()
                Log.d(Constants.GIL_TAG, "Name: $userName")
                Log.d(Constants.GIL_TAG, "id: $deviceId")
                Log.d(Constants.GIL_TAG, "id: $userDateCreated")
                retrofit = RetrofitHelper().getRetrofit()
                repository = GilRepository(retrofit)

                lifecycleScope.launch {
                    try {
                        val snackbar : Snackbar = Snackbar.make(view,"", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(requireContext().getColor(R.color.red))

                        val newUser = UserDto(name = userName, email = userEmail, password = userPass, deviceId = deviceId, createdAt = userDateCreated)

                        val register = repository.postRegUser(newUser)
                        Log.d(Constants.GIL_TAG, "Respuesta: $register")
                        Log.d(Constants.GIL_TAG, "Respuesta: $newUser")

                        if (register.code() == 200){
                            snackbar.setText("Registro exitoso!")
                            snackbar.setBackgroundTint(requireContext().getColor(R.color.green))
                            findNavController().navigateUp()
                        }else{


                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Error en la conexion", Toast.LENGTH_SHORT).show()
                        Log.d(Constants.GIL_TAG, "Error en la conexion")
                    }

                }

                //Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun validateInputSignUp(
        name: String,
        email: String,
        emailconf: String,
        pass: String,
        passconf: String
    ): Boolean {
        var isValid = true

        if (name.isEmpty() || name.length < 3) {
            binding.lbSignName.error = getString(R.string.invalid_name)
            isValid = false
        }

        if (email != emailconf) {
            binding.lbSignEmail.error = getString(R.string.not_same_value)
            binding.lbSignEmailConf.error = getString(R.string.not_same_value)
            isValid = false
        } else {
            binding.lbSignEmail.error = null
            binding.lbSignEmailConf.error = null
            binding.lbSignEmail.isErrorEnabled = false
            binding.lbSignEmailConf.isErrorEnabled = false
        }

        if (!UIUtils.checkEmail(email)) {
            binding.lbSignEmail.error = getString(R.string.not_valid_email)
            isValid = false
        }

        if (!UIUtils.checkEmail(emailconf)) {
            binding.lbSignEmailConf.error = getString(R.string.not_valid_email)
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

        if (pass != passconf) {
            binding.lbSignPass.error = getString(R.string.not_same_value)
            binding.lbSignPassConf.error = getString(R.string.not_same_value)
            isValid = false
        } else {
            binding.lbSignPass.error = null
            binding.lbSignPassConf.error = null
            binding.lbSignPass.isErrorEnabled = false
            binding.lbSignPassConf.isErrorEnabled = false
        }

        if (pass.isEmpty() || pass.length < 8) {
            binding.lbSignPass.error = getString(R.string.invalid_password)
            isValid = false
        }

        if (passconf.isEmpty() || pass.length < 8) {
            binding.lbSignPassConf.error = getString(R.string.invalid_confirmPass)
            isValid = false
        }

        return isValid
    }


    private fun focusAndTextListener() {
        UIUtils.setupFocusAndTextListener(binding.etSignName, binding.lbSignName)
        UIUtils.setupFocusAndTextListener(binding.etSignEmail, binding.lbSignEmail)
        UIUtils.setupFocusAndTextListener(binding.etSignEmailConf, binding.lbSignEmailConf)
        UIUtils.setupFocusAndTextListener(binding.etSignPass, binding.lbSignPass)
        UIUtils.setupFocusAndTextListener(binding.etSignPassConf, binding.lbSignPassConf)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}