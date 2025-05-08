package com.jbg.gil.features.login.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jbg.gil.R
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.databinding.FragmentLoginBinding
import com.jbg.gil.features.home.ui.HomeActivity
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.showSnackBar


class LogInFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel : LogInViewModel by viewModels()
    private val networkViewModel: NetworkStatusViewModel by viewModels()

    private var isConnectedApp : Boolean = false

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
        networkViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            isConnectedApp = isConnected
        }

        viewModel.clearFieldErrors()//Clear Error

        binding.apply {
            etLogUser.doAfterTextChanged { value ->
                viewModel.email.value = value.toString().trim()
            }
            etLogPass.doAfterTextChanged { value ->
                viewModel.password.value = value.toString().trim()
            }

            tvRegister.setOnClickListener {

                binding.tvRegister.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.secondary
                    )
                )

               /* Handler(Looper.getMainLooper()).postDelayed({
                    DialogUtils.dismissLoadingDialog()
                }, 5000) // Espera 3 segundos (3000 milisegundos)*/
                //showLoadingDialog()
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

            invalidCredentials.observe(viewLifecycleOwner) { error ->
                if (error) {
                    binding.root.showSnackBar(
                        getString(R.string.invalid_data),
                        backgroundColor = R.color.red,
                        actionText = getString(R.string.close)
                    )
                    viewModel.invalidCredentials.value = false
                }
            }

            serverError.observe(viewLifecycleOwner) { error ->
                if(error){
                    binding.root.showSnackBar(
                        getString(R.string.server_error),
                        backgroundColor = R.color.yellow,
                        actionText = getString(R.string.close)
                    )
                    viewModel.serverError.value = false
                }

            }

            loginSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    DialogUtils.dismissLoadingDialog()
                    val startIntentH =
                        Intent(requireContext(), HomeActivity::class.java)
                    startActivity(startIntentH)
                }
            }

        }


        binding.btLogIn.setOnClickListener {

            if (isConnectedApp) {
                //Connected
                //DialogUtils.showLoadingDialog(requireActivity())
                viewModel.logIn()

            } else {
                // No connected
                binding.root.showSnackBar(
                    getString(R.string.no_internet_connection),
                    backgroundColor = R.color.red,
                    actionText = getString(R.string.close)
                )

            }


        }

    }
    //? = null
   /* fun showLoadingDialog() {
        val loadingDialogFragment = LoadingDialogFragment()
        loadingDialogFragment.show(childFragmentManager, "loadingDialog")
    }

    fun hideLoadingDialog() {
        val fragment = childFragmentManager.findFragmentByTag("loadingDialog")
        (fragment as? LoadingDialogFragment)?.dismiss()
    }*/

    private fun focusAndTextListener() {
        Utils.setupFocusAndTextListener(binding.etLogUser, binding.lbLogUser)
        Utils.setupFocusAndTextListener(binding.etLogPass, binding.lbLogPass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}