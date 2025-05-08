package com.jbg.gil.features.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jbg.gil.databinding.FragmentSettingsBinding
import com.jbg.gil.features.login.ui.LogInActivity
import com.jbg.gil.core.utils.Utils.clearUserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private lateinit var binding : FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btLogOut.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                clearUserPreferences(requireContext())
                val intent = Intent(requireContext(), LogInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //val effect = ActivityOptions.makeCustomAnimation(requireContext(),R.animator.card_flip_in, R.animator.card_flip_out)
                //startActivity(intent, effect.toBundle())
                startActivity(intent)
                /*@Suppress("DEPRECATION")
                requireActivity().overridePendingTransition(R.anim.card_flip_in, R.anim.card_flip_out)*/
            }

        }
    }

}