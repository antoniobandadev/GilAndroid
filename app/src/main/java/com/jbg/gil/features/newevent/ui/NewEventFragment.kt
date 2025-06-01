package com.jbg.gil.features.newevent.ui


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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.jbg.gil.R
import com.jbg.gil.core.data.model.EntityDtoMapper.toEntity
import com.jbg.gil.core.data.remote.dtos.EventDto
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.network.NetworkStatusViewModel
import com.jbg.gil.core.repositories.ContactRepository
import com.jbg.gil.core.repositories.EventRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.convertDate
import com.jbg.gil.core.utils.Utils.copyUriToInternalStorage
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.prepareImagePart
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentNewEventBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NewEventFragment () : Fragment() {

    private  var _binding : FragmentNewEventBinding? = null
    private val binding get() = _binding!!

    private var  eventType : String = ""

    @Inject
    lateinit var eventRepository: EventRepository
    @Inject
    lateinit var contactRepository: ContactRepository
    @Inject
    lateinit var userPreferences: UserPreferences

    private val locale = Locale.getDefault().language
    private val strDateFormat = when (locale) {
        "es" -> "dd/MM/yyyy"
        "en" -> "MM/dd/yyyy"
        else -> "MM/dd/yyyy"
    }
    private val strDateFormatBD = "yyyy-MM-dd"

    private val networkStatusViewModel : NetworkStatusViewModel by viewModels()

    private var eventImage : MultipartBody.Part? = null

    private lateinit var imageSelected : ImageView
    private lateinit var textAdd : TextView
    private lateinit var textDelete : TextView

    private var uriDB : String = "null"

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
         eventImage = prepareImagePart(uri, requireContext(), "eventImage")
        if (uri != null){
            uriDB = copyUriToInternalStorage(requireContext(),uri).toString()
            Log.d(Constants.GIL_TAG, "Imagen seleccionada")
            imageSelected.visibility = View.VISIBLE
            imageSelected.setImageURI(uri)
            textAdd.setText(R.string.edit_image)
            textDelete.visibility = View.VISIBLE
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
        // Inflate the layout for this fragment
        _binding = FragmentNewEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setupHideKeyboardOnTouch(binding.root, requireActivity())
        focusAndTextListener()
        loadEventTypes()
        loadFriends()

        imageSelected = binding.ivEvent
        textAdd = binding.tvAddImage
        textDelete = binding.tvDeleteImage

        networkStatusViewModel.getNetworkStatus().observe(viewLifecycleOwner) { isConnected ->
            Log.d(Constants.GIL_TAG, "Status: $isConnected")
            if (!isConnected){
                DialogUtils.dismissLoadingDialog()
            }else{
                eventsPendingDB()
                Log.d(Constants.GIL_TAG, "Actualizando")
            }
        }

        binding.tvAddImage.setOnClickListener {
            binding.tvAddImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
           // binding.tvAddImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
           //Tipo en especifico
           // pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("img/gif")))
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        binding.tvDeleteImage.setOnClickListener {
            eventImage = null
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

            if(validateInputs()){
                DialogUtils.showLoadingDialog(requireContext())
                if (Utils.isConnectedNow(requireContext())) {
                    try {
                        binding.apply {

                            lifecycleScope.launch {

                                val etEventDateStartDB = convertDate(etEventDateStart.text.toString(), strDateFormat, strDateFormatBD)
                                val etEventDateEndDB = convertDate(etEventDateEnd.text.toString(), strDateFormat, strDateFormatBD)
                                val eventId = Utils.createPartFromString("new")
                                val eventName =
                                    Utils.createPartFromString(etEventName.text.toString())
                                val eventDesc =
                                    Utils.createPartFromString(etEventDesc.text.toString())
                                val eventTypeBody = Utils.createPartFromString(eventType)
                                val eventDateStart =
                                    Utils.createPartFromString(etEventDateStartDB.toString())
                                val eventDateEnd =
                                    Utils.createPartFromString(etEventDateEndDB.toString())
                                val eventStreet =
                                    Utils.createPartFromString(etEventStreet.text.toString())
                                val eventCity =
                                    Utils.createPartFromString(etEventCity.text.toString())
                                val eventStatus = Utils.createPartFromString("A")
                                val userId = Utils.createPartFromString(
                                    userPreferences.getUserId().toString()
                                )

                                val eventInsert = eventRepository.uploadEvent(
                                    eventImage,
                                    eventId,
                                    eventName,
                                    eventDesc,
                                    eventTypeBody,
                                    eventDateStart,
                                    eventDateEnd,
                                    eventStreet,
                                    eventCity,
                                    eventStatus,
                                    userId
                                )

                                if (eventInsert.isSuccessful) {
                                    DialogUtils.dismissLoadingDialog()
                                    root.showSnackBar(getString(R.string.event_save_success))
                                    clearAll()
                                    requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
                                        .selectedItemId = R.id.eventsFragment
                                } else {
                                    DialogUtils.dismissLoadingDialog()
                                    root.showSnackBarError(getString(R.string.server_error))
                                }

                            }

                        }
                    }catch (e:Exception){
                        DialogUtils.dismissLoadingDialog()
                        getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                    }
                }else{
                    val currentDate = Date()
                    val formatter = SimpleDateFormat(strDateFormatBD, Locale.getDefault())
                    val dateNow = formatter.format(currentDate)
                    val imagePath = uriDB

                    val etEventDateStartDB = convertDate(binding.etEventDateStart.text.toString(), strDateFormat, strDateFormatBD)
                    val etEventDateEndDB = convertDate(binding.etEventDateEnd.text.toString(), strDateFormat, strDateFormatBD)

                    val myEvent = EventDto(
                        eventId = UUID.randomUUID().toString(),
                        eventName = binding.etEventName.text.toString(),
                        eventDesc = binding.etEventDesc.text.toString(),
                        eventType = eventType,
                        eventDateStart =etEventDateStartDB,
                        eventDateEnd = etEventDateEndDB,
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
                                eventRepository.insertEventDB(myEvent.toEntity())
                            }
                            result.await()
                            getActivityRootView()?.showSnackBar(getString(R.string.event_save_success))
                            clearAll()
                            requireActivity().findViewById<BottomNavigationView>(R.id.botHomMenu)
                                .selectedItemId = R.id.eventsFragment
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

    private fun loadEventTypes(){
        val typesEvents = Utils.getEventTypes(requireContext())

        binding.acEventType.setDropDownBackgroundResource(android.R.color.transparent)


        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, typesEvents)
        binding.acEventType.setAdapter(adapter)
    }

    private fun loadFriends(){
        lifecycleScope.launch {
            if (Utils.isConnectedNow(requireContext())) {

                val updateFriends = contactRepository.getFriendsToContacts(userPreferences.getUserId().toString())
                if (updateFriends.isSuccessful){
                    contactRepository.deleteFriendsToContacts()
                    val contactList = updateFriends.body() ?: emptyList()
                    val contacts = contactList.map { contact ->
                        contact.toEntity()
                    }

                    contactRepository.insertContactList(contacts)
                }


                val friends = contactRepository.getFriendsApi(userPreferences.getUserId().toString(), "A")
                    binding.acUserScan.setDropDownBackgroundResource(android.R.color.transparent)

                if (friends.isSuccessful) {
                    Log.d(Constants.GIL_TAG, "Select Friends")
                    val friendList = friends.body()
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_dropdown,
                        friendList?.map { it.contactName } ?: emptyList())
                    binding.acUserScan.setAdapter(adapter)
                }
            }else{
                val friends = contactRepository.getFriendsDB()
                binding.acUserScan.setDropDownBackgroundResource(android.R.color.transparent)
                if (friends.isNotEmpty()){
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_dropdown,
                        friends.map { it.contactName }
                    )
                    binding.acUserScan.setAdapter(adapter)
                }
            }

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

    private fun clearAll(){
        binding.apply {
            etEventName.text?.clear()
            etEventDesc.text?.clear()
            acEventType.text?.clear()
            etEventDateStart.text?.clear()
            etEventDateEnd.text?.clear()
            etEventStreet.text?.clear()
            etEventCity.text?.clear()
        }
    }

    private fun eventsPendingDB(){
        lifecycleScope.launch {
            val events = eventRepository.getEventSyncDB()
            for (event in events) {
                try {
                    val eventId = Utils.createPartFromString(event.eventId)
                    val eventName =
                        Utils.createPartFromString(event.eventName)
                    val eventDesc =
                        Utils.createPartFromString(event.eventDesc)
                    val eventTypeBody = Utils.createPartFromString(event.eventType)
                    val eventDateStart =
                        Utils.createPartFromString(event.eventDateStart)
                    val eventDateEnd =
                        Utils.createPartFromString(event.eventDateEnd)
                    val eventStreet =
                        Utils.createPartFromString(event.eventStreet)
                    val eventCity =
                        Utils.createPartFromString(event.eventCity)
                    val eventStatus = Utils.createPartFromString(event.eventStatus)
                    val userId = Utils.createPartFromString(event.userId)

                    val eventImage = event.eventImg.let { path ->
                        val file = File(path)
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("eventImage", file.name, requestFile)
                    }

                    val response = eventRepository.uploadEvent(
                        eventImage,
                        eventId,
                        eventName,
                        eventDesc,
                        eventTypeBody,
                        eventDateStart,
                        eventDateEnd,
                        eventStreet,
                        eventCity,
                        eventStatus,
                        userId
                    )

                    if (response.isSuccessful) {
                        val myEvent = response.body()
                        eventRepository.updateEventSyncDB(myEvent?.eventId.toString(),myEvent?.eventImg.toString())
                    }



                } catch (e: Exception) {
                    Log.e("SyncWorker", "Error al subir evento imagen: ${e.message}", e)
                }
            }

        }
    }






    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}