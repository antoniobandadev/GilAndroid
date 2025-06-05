package com.jbg.gil.features.scan.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.databinding.FragmentScannerBinding

class ScannerFragment : Fragment() {


    private var _binding: FragmentScannerBinding?=null
    private val binding get() = _binding!!

    //Permisos de la camara 24 - 53, 70 -87
    private var cameraPermissionGranted = false

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted ->
        if (isGranted){
            //Con permiso
            Log.d(Constants.GIL_TAG, "Ready to scan")
            actionPermissionGranted()

        }else{
            //Sin permiso
            //Revisamos si nego permanentemente el permiso
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                Utils.showOkAlertDialogPositiveA(
                    requireContext(),
                    getString(R.string.permission_title),
                    getString(R.string.permission_message),
                    getString(R.string.ok),
                    onConfirm = {
                        updateOrRequestPermissions()
                    }

                )


            } else {
                Utils.showConfirmAlertDialog(
                    requireContext(),
                    getString(R.string.permission_title),
                    getString(R.string.permission_message),
                    getString(R.string.open_settings),
                    getString(R.string.close),
                    onConfirm = {
                        openedSettings = true
                        val intent = Intent( Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", requireContext().packageName, null)
                        }
                        requireActivity().startActivity(intent)
                    }
                )

            }
        }

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateOrRequestPermissions()
        backAction()

    }

    private fun updateOrRequestPermissions(){
        //Reviso si tengo permiso
        cameraPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if(!cameraPermissionGranted){
            //No tengo permiso
            permissionsLauncher.launch(Manifest.permission.CAMERA)
        }else{
            //tengo permiso
            Log.d(Constants.GIL_TAG, "Ready to scan")
            actionPermissionGranted()
        }

    }

    override fun onStart() {
        super.onStart()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
        bottomNavView.menu.findItem(R.id.scanFragment).isChecked = true
        //Log.d(Constants.GIL_TAG, bottomNavView.selectedItemId.toString())
    }


    override fun onResume() {
        super.onResume()
        binding.cbvScanner.resume()
        if (openedSettings) {
            updateOrRequestPermissions()
            openedSettings = false
        }
        Log.d(Constants.GIL_TAG, "RESUME")
    }

    override fun onPause() {
        super.onPause()
        binding.cbvScanner.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun actionPermissionGranted(){
        //Iniciamos el scanner
        binding.cbvScanner.decodeContinuous { result ->
            Toast.makeText(
                requireContext(),
                "Result: ${result.text}",
                Toast.LENGTH_SHORT
            ).show()

            Log.d(Constants.GIL_TAG, "Result: ${result.text}")

            findNavController().navigate(R.id.action_scannerFragment_to_scanFragment)
            binding.cbvScanner.pause()

        }
    }

    private fun backAction() {
        binding.imgBtBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigateUp()
        }
        binding.tvBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigateUp()
        }
    }

    companion object {
        var openedSettings = false
    }



}