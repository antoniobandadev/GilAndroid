package com.jbg.gil.features.scan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jbg.gil.databinding.FragmentScanBinding


class ScanFragment : Fragment() {

    private lateinit var binding : FragmentScanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }
}