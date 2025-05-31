package com.jbg.gil.features.events.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.datepicker.MaterialDatePicker
import com.jbg.gil.R
import com.jbg.gil.core.data.model.EntityDtoMapper.toEntity
import com.jbg.gil.core.data.remote.dtos.EventDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.repositories.EventRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.core.utils.Utils.convertDate
import com.jbg.gil.core.utils.Utils.copyUriToInternalStorage
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.prepareImagePart
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentEventsDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@AndroidEntryPoint
class EventsDetailFragment : Fragment() {

    private var _binding : FragmentEventsDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var eventRepository: EventRepository
    @Inject
    lateinit var userPreferences: UserPreferences

    private val args: EventsDetailFragmentArgs by navArgs()

    private var  eventType : String = ""

    private val locale = Locale.getDefault().language
    private val strDateFormat = when (locale) {
        "es" -> "dd/MM/yyyy"
        "en" -> "MM/dd/yyyy"
        else -> "MM/dd/yyyy"
    }
    private val strDateFormatBD = "yyyy-MM-dd"

    private var eventImage : MultipartBody.Part? = null

    private lateinit var imageSelected : ImageView
    private lateinit var textAdd : TextView
    private lateinit var textDelete : TextView

    private var uriDB : String = "null"
    private var imagePath : String = "null"

        private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        eventImage = prepareImagePart(uri, requireContext(), "eventImage")
        if (uri != null){
            uriDB = copyUriToInternalStorage(requireContext(),uri).toString()
            Log.d(Constants.GIL_TAG, "Imagen seleccionada")
            imageSelected.visibility = View.VISIBLE
            imageSelected.setImageURI(uri)
            textDelete.visibility = View.VISIBLE
            textAdd.setText(R.string.edit_image)
            textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyDark_load))

        }else{
            textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyDark_load))
            /*uriDB = "null"
            Log.d(Constants.GIL_TAG, "Imagen NO seleccionada")
            imageSelected.visibility = View.GONE
            imageSelected.setImageResource(R.drawable.ic_add_image)
            textAdd.setText(R.string.add_image)
            textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))*/
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setupHideKeyboardOnTouch(binding.root, requireActivity())
        focusAndTextListener()
        backAction()

        imageSelected = binding.ivEvent
        textAdd = binding.tvAddImage
        textDelete = binding.tvDeleteImage

        val eventId = args.eventId
        Log.d(Constants.GIL_TAG, "Event: $eventId")
        loadInfoEvent(eventId)

        binding.tvAddImage.setOnClickListener {
            binding.tvAddImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            // binding.tvAddImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            //Tipo en especifico
            // pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("img/gif")))
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
        binding.tvDeleteImage.setOnClickListener {
            uriDB = "null"
            Log.d(Constants.GIL_TAG, "Imagen NO seleccionada")
            imageSelected.visibility = View.GONE
            imageSelected.setImageResource(R.drawable.ic_add_image)
            textAdd.setText(R.string.add_image)
            textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyDark_load))
            textDelete.visibility = View.GONE

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

            if(validateInputs()) {
                DialogUtils.showLoadingDialog(requireContext())

                val currentDate = Date()
                val formatter = SimpleDateFormat(strDateFormatBD, Locale.getDefault())
                val dateNow = formatter.format(currentDate)


                imagePath = uriDB


                val etEventDateStartDB = convertDate(binding.etEventDateStart.text.toString(), strDateFormat, strDateFormatBD)
                val etEventDateEndDB = convertDate(binding.etEventDateEnd.text.toString(), strDateFormat, strDateFormatBD)

                val myEvent = EventDto(
                    eventId= eventId,
                    eventName = binding.etEventName.text.toString(),
                    eventDesc = binding.etEventDesc.text.toString(),
                    eventType = eventType,
                    eventDateStart = etEventDateStartDB,
                    eventDateEnd =etEventDateEndDB,
                    eventStreet = binding.etEventStreet.text.toString(),
                    eventCity = binding.etEventCity.text.toString(),
                    eventStatus = "A",
                    eventImg = imagePath,
                    eventCreatedAt = dateNow,
                    userId = userPreferences.getUserId().toString(),
                    eventSync = 0
                )

                try {
                    lifecycleScope.launch {

                        val result = async {
                            eventRepository.updateEvent(myEvent.toEntity())
                        }
                        result.await()
                        Thread.sleep(2000)
                        getActivityRootView()?.showSnackBar(getString(R.string.event_update_success))
                        findNavController().navigate(EventsDetailFragmentDirections.actionEventsDetailFragmentToEventsFragment())
                        DialogUtils.dismissLoadingDialog()
                    }

                } catch (e: Exception) {
                    Log.d(Constants.GIL_TAG, "Error Insert Event: $e")
                    DialogUtils.dismissLoadingDialog()
                    binding.root.showSnackBarError(getString(R.string.server_error))
                }
            }
        }


    }


    private fun loadInfoEvent(eventId : String){
        lifecycleScope.launch {

            val events = eventRepository.getEvent(eventId)

            if (events.isNotEmpty()) {
                val event = events.first()

                val eventStartDateFormat = Utils.convertDate(event.eventDateStart, strDateFormatBD, strDateFormat)
                val eventEndDateFormat = Utils.convertDate(event.eventDateEnd, strDateFormatBD, strDateFormat)

                binding.apply {
                    btSaveEvent.setText(R.string.update)
                    tvTitleEvents.text = getString(R.string.edit_event)
                    etEventName.setText(event.eventName)
                    etEventDesc.setText(event.eventDesc)
                    loadEventTypes(event.eventType)
                    etEventDateStart.setText(eventStartDateFormat)
                    etEventDateEnd.setText(eventEndDateFormat)
                    etEventStreet.setText(event.eventStreet)
                    etEventCity.setText(event.eventCity)

                    if (event.eventImg != "null") {
                        imagePath = event.eventImg

                        Glide.with(binding.root.context)
                            .load(event.eventImg)
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable,
                                    transition: Transition<in Drawable>?
                                ) {
                                    ivEvent.setImageDrawable(resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }

                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    ivEvent.setImageDrawable(errorDrawable)
                                }
                            })

                        ivEvent.visibility = View.VISIBLE
                        tvAddImage.text = getString(R.string.edit_image)
                        tvDeleteImage.visibility = View.VISIBLE

                    }else{

                    }

                    showData()

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

            if(!Utils.isDateEarlierToday(etEventDateStart.text.toString(), strDateFormat)){
                lbEventDateStart.error = getString(R.string.invalid_date)
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


    private fun loadEventTypes(typeSelected : String){
        val typesEvents = Utils.getEventTypes(requireContext())

        binding.acEventType.setDropDownBackgroundResource(android.R.color.transparent)


        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, typesEvents)
        binding.acEventType.setAdapter(adapter)

        if (typesEvents.contains(typeSelected)) {
            binding.acEventType.setText(typeSelected, false)
            eventType = typeSelected
        }

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



    private fun showData() {
        binding.viewEventDetLoad.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
    }

    private fun backAction() {
        binding.imgBtBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigate(EventsDetailFragmentDirections.actionEventsDetailFragmentToEventsFragment())
        }
        binding.tvBack.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            binding.tvBack.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            binding.imgBtBack.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent))
            findNavController().navigate(EventsDetailFragmentDirections.actionEventsDetailFragmentToEventsFragment())
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}