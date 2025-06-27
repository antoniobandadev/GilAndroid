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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.delay
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jbg.gil.R
import com.jbg.gil.core.repositories.GuestRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentScannerBinding
import com.jbg.gil.features.events.ui.fragments.EventsDetailFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : Fragment() {


    private var _binding: FragmentScannerBinding?=null
    private val binding get() = _binding!!

    @Inject
    lateinit var guestRepository: GuestRepository

    private lateinit var eventId: String
    private val args: EventsDetailFragmentArgs by navArgs()

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
        eventId = args.eventId
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
            /*Toast.makeText(
                requireContext(),
                "Result: ${result.text}",
                Toast.LENGTH_SHORT
            ).show()*/

            binding.cbvScanner.pause()
            DialogUtils.showLoadingDialog(requireContext())

            if(Utils.isConnectedNow(requireContext())) {
                Utils.vibrate(requireContext())
                try {
                    lifecycleScope.launch {
                        val scanVal = guestRepository.checkGuest(result.text, eventId)

                        if (scanVal.isSuccessful) {
                            val scan = scanVal.body()
                            if (scan?.guestsResponse.toString() == "2") {
                                DialogUtils.updateLoadingDialogCorrect(requireContext())
                                delay(3_000)
                                DialogUtils.dismissLoadingDialog()
                                binding.cbvScanner.pause()
                            } else if (scan?.guestsResponse.toString() == "1") {
                                DialogUtils.updateLoadingDialogScanned(requireContext())
                                delay(3_000)
                                DialogUtils.dismissLoadingDialog()
                                binding.cbvScanner.pause()

                            } else if (scan?.guestsResponse.toString() == "0") {
                                DialogUtils.updateLoadingDialogInvalid(requireContext())
                                delay(3_000)
                                DialogUtils.dismissLoadingDialog()
                                binding.cbvScanner.pause()
                                Log.d(Constants.GIL_TAG, "Validando")
                            }
                        } else {
                            DialogUtils.dismissLoadingDialog()
                            getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                        }


                        view?.post {
                            if (isAdded && view != null) {
                                findNavController().navigate(R.id.action_scannerFragment_to_scanFragment)
                                Log.d(Constants.GIL_TAG, "Salir")
                            }
                        }
                       // findNavController().navigate(R.id.action_scannerFragment_to_scanFragment)
                    }



                } catch (e: Exception) {
                    DialogUtils.dismissLoadingDialog()
                    getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                }
            }else{
                DialogUtils.dismissLoadingDialog()
                getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
            }



           // findNavController().navigateUp()
            Log.d(Constants.GIL_TAG, "Result: ${result.text}")



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