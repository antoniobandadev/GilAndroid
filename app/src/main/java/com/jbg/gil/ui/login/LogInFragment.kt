package com.jbg.gil.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.jbg.gil.R
import com.jbg.gil.databinding.FragmentLoginBinding
import com.jbg.gil.utils.UIUtils


class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusAndTextListener()

        binding.btLogIn.setOnClickListener {
            if (validateInputLogin(
                    binding.etLogUser.text.toString().trim(),
                    binding.etLogPass.text.toString().trim()
                )
            ) {
                Toast.makeText(requireContext(), "Bienvenido", Toast.LENGTH_SHORT).show()
            }

            //val startIntentH = Intent(this, HomeActivity::class.java)
            //if(binding.etLogUser.text.toString() == "Antonio"){
            //    startActivity(startIntentH)
            //}else{
            //   binding.lbLogUser.error = "Error"
            //    Toast.makeText(this, getString(R.string.invalid_data), Toast.LENGTH_SHORT).show()
            //}
        }

        binding.tvRegister.setOnClickListener {
            binding.tvRegister.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.secondary
                )
            )

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(R.id.fmLogActivity, SignUpFragment.newInstance())
                .addToBackStack("Login")
                .commit()


        }


    }


    companion object {
        @JvmStatic
        fun newInstance() = LogInFragment()
    }

    private fun validateInputLogin(user: String, password: String): Boolean {
        var isValid = true

        if (user.isEmpty()) {
            binding.lbLogUser.error = getString(R.string.invalid_username)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.lbLogPass.error = getString(R.string.invalid_password)
            isValid = false
        }

        return isValid
    }


    private fun focusAndTextListener() {
        UIUtils.setupFocusAndTextListener(binding.etLogUser, binding.lbLogUser)
        UIUtils.setupFocusAndTextListener(binding.etLogPass, binding.lbLogPass)
    }


    /* private fun editTextListener() {
         binding.etLogUser.setOnFocusChangeListener { view , hasFocus ->
             if (hasFocus){
                 binding.lbLogUser.error = null
                 binding.lbLogUser.isErrorEnabled = false
             }
         }

         binding.etLogPass.setOnFocusChangeListener { view, hasFocus ->
             if (hasFocus){
                 binding.lbLogPass.error = null
                 binding.lbLogPass.isErrorEnabled = false
             }
         }

         binding.etLogUser.addTextChangedListener {
             binding.lbLogUser.error = null
             binding.lbLogUser.isErrorEnabled = false

         }

         binding.etLogPass.addTextChangedListener {
             binding.lbLogPass.error = null
             binding.lbLogPass.isErrorEnabled = false
         }

     }*/


}