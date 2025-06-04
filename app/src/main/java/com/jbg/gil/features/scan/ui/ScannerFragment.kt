package com.jbg.gil.features.scan.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jbg.gil.R
import com.jbg.gil.core.utils.Constants
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
            Log.d(Constants.GIL_TAG, "listo para scanear")
            actionPermissionGranted()

        }else{
            //Sin permiso
            //Revisamos si nego permanentemente el permiso
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                //Explicamos para que se necesita el permiso
                AlertDialog.Builder(requireContext())
                    .setTitle("Permiso")
                    .setMessage("Se necesita el permiso solamente para leer codigo QR.")
                    .setPositiveButton("Ok"){_,_ ->
                        //Volvemos a pedir el permiso
                        updateOrRequestPermissions()
                    }
                    .setNegativeButton("cancel"){dialog,_ ->
                        dialog.dismiss()
                        requireActivity().finish()
                    }
            }else{
                //Se nego permanete mente
                Toast.makeText(requireContext(), "Se denego el permiso permanente", Toast.LENGTH_SHORT).show()
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
            Log.d(Constants.GIL_TAG, "listo para scanear2")
            actionPermissionGranted()
        }

    }


    override fun onResume() {
        super.onResume()
        binding.cbvScanner.resume()
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
                "Resultado: ${result.text}",
                Toast.LENGTH_SHORT
            ).show()

            Log.d("APPLOGS", "Resultado: ${result.text}")

            findNavController().navigate(R.id.action_scannerFragment_to_scanFragment)
            binding.cbvScanner.pause()

            /*try{
                URL(result.text)

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = result.text.toUri()
                startActivity(intent)

            }catch(e: MalformedURLException){
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("El código no es válido para la aplicación")
                    .setNeutralButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create()
                    .show()
            }*/
        }
    }


}