package com.jbg.gil.features.newevent.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.jbg.gil.R
import com.jbg.gil.core.data.remote.apis.EventApi
import com.jbg.gil.core.data.remote.dtos.EventDto
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentNewEventBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@AndroidEntryPoint
class NewEventFragment () : Fragment() {

    private  var _binding : FragmentNewEventBinding? = null
    private val binding get() = _binding!!

    private var  eventType : String = ""

    @Inject
    lateinit var eventApi : EventApi

    private val networkStatusViewModel : NetworkStatusViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if (uri != null){
            Log.d(Constants.GIL_TAG, "Imagen seleccionada")
        }else{
            Log.d(Constants.GIL_TAG, "Imagen NO seleccionada")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setupHideKeyboardOnTouch(binding.root, requireActivity())
        focusAndTextListener()
        loadEventTypes()

        networkStatusViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            Log.d(Constants.GIL_TAG, "Status: $isConnected")
            if (!isConnected){
                DialogUtils.dismissLoadingDialog()
            }
        }

        binding.ivEvent.setOnClickListener {
            binding.ivEvent.background = Utils.showRipple(requireContext())
           //Tipo en especifico
           // pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("img/gif")))
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }


        binding.acEventType.setOnItemClickListener { parent, _, position, _ ->
            eventType = parent.getItemAtPosition(position) as String
        }

        binding.etEventDateStart.setOnClickListener {
            showCalendar()
        }

        binding.etEventDateEnd.setOnClickListener {
            showCalendar()
        }

        binding.btSaveEvent.setOnClickListener {
            DialogUtils.showLoadingDialog(requireContext())
            if(validateInputs()){
                if (Utils.isConnectedNow(requireContext())) {

                    binding.apply {

                        lifecycleScope.launch {
                            val myNewEvent = EventDto(
                                eventName = etEventName.text.toString(),
                                eventDesc = etEventDesc.text.toString(),
                                eventType = eventType,
                                eventDateStart = etEventDateStart.text.toString(),
                                eventDateEnd = etEventDateEnd.text.toString(),
                                eventStreet = etEventStreet.text.toString(),
                                eventCity = etEventCity.text.toString()
                            )

                            val eventInsert = eventApi.newEvent(myNewEvent)

                            if (eventInsert.isSuccessful){
                                DialogUtils.dismissLoadingDialog()
                                root.showSnackBar(getString(R.string.event_save_success))
                            }else{
                                DialogUtils.dismissLoadingDialog()
                                root.showSnackBarError(getString(R.string.server_error))
                            }

                        }

                    }
                }else{
                    DialogUtils.dismissLoadingDialog()
                    binding.root.showSnackBarError(getString(R.string.no_internet_connection))
                }
            }
        }
    }

    private fun validateInputs() : Boolean{
        binding.apply {
            if (etEventName.text.toString().isBlank()){
                lbEventName.error = getString(R.string.required_field)
                return false
            }

            if (etEventDesc.text.toString().isBlank()){
                lbEventDesc.error = getString(R.string.required_field)
                return false
            }

            if (eventType.isBlank()){
                lbEventType.error = getString(R.string.required_field)
                return false
            }

            if (etEventDateStart.text.toString().isBlank()){
                lbEventDateStart.error = getString(R.string.required_field)
                return false
            }

            if (etEventDateEnd.text.toString().isBlank()){
                lbEventDateEnd.error = getString(R.string.required_field)
                return false
            }

            if (etEventStreet.text.toString().isBlank()){
                lbEventStreet.error = getString(R.string.required_field)
                return false
            }

            if (etEventCity.text.toString().isBlank()){
                lbEventCity.error = getString(R.string.required_field)
                return false
            }
        }

        return true
    }

    private fun loadEventTypes(){
        val typesEvents = listOf(getString(R.string.event_category_social),
            getString(R.string.event_category_corporate),
            getString(R.string.event_category_academic),
            getString(R.string.event_category_other)
        )

        binding.acEventType.setDropDownBackgroundResource(android.R.color.transparent)


        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, typesEvents)
        binding.acEventType.setAdapter(adapter)
    }

    private fun showCalendar(){
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .build()

        binding.spDate.visibility = View.VISIBLE
        picker.show(parentFragmentManager, "DATE_PICKER")

        picker.addOnDismissListener {
            binding.spDate.visibility = View.GONE
        }

        picker.addOnPositiveButtonClickListener { selection ->
            binding.spDate.visibility = View.GONE

            val startDate = selection.first
            val endDate = selection.second

            val locale = Locale.getDefault().language
            val strDateFormat = when (locale) {
                "es" -> "dd/MM/yyyy"
                "en" -> "MM/dd/yyyy"
                else -> "MM/dd/yyyy"
            }

            val formatter = SimpleDateFormat(strDateFormat , Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")

            val formattedDateIni = formatter.format(Date(startDate))
            val formattedDateEnd = formatter.format(Date(endDate))
            binding.etEventDateStart.setText(formattedDateIni)
            binding.etEventDateEnd.setText(formattedDateEnd)
        }
    }


    private fun focusAndTextListener() {
        binding.apply {
            Utils.setupFocusAndTextListener(etEventName, lbEventName)
            Utils.setupFocusAndTextListener(etEventDesc, lbEventDesc)
            Utils.setupFocusAndTextListener(acEventType, lbEventType)
            Utils.setupFocusAndTextListener(etEventDateStart, lbEventDateStart)
            Utils.setupFocusAndTextListener(etEventDateEnd, lbEventDateEnd)
            Utils.setupFocusAndTextListener(etEventStreet, lbEventStreet)
            Utils.setupFocusAndTextListener(etEventCity, lbEventCity)
        }
    }






    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}