package com.jbg.gil.features.settings.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jbg.gil.R
import com.jbg.gil.core.datastore.UserPreferences
import com.jbg.gil.core.repositories.UserRepository
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.DialogUtils
import com.jbg.gil.core.utils.Utils
import com.jbg.gil.core.utils.Utils.applyClickAnimation
import com.jbg.gil.core.utils.Utils.getActivityRootView
import com.jbg.gil.core.utils.Utils.prepareImagePart
import com.jbg.gil.core.utils.Utils.showSnackBar
import com.jbg.gil.core.utils.Utils.showSnackBarError
import com.jbg.gil.databinding.FragmentSettingsBinding
import com.jbg.gil.features.login.ui.LogInActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding?=null
    private val binding get() = _binding!!

    @Inject
    lateinit var userPreferences: UserPreferences
    @Inject
    lateinit var userRepository: UserRepository


    private var userProfile : MultipartBody.Part? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


         val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->

            if (uri != null){
                if (Utils.isConnectedNow(requireContext())){
                    DialogUtils.showLoadingDialog(requireContext())
                    userProfile = prepareImagePart(uri, requireContext(), "userProfile")
                    Log.d(Constants.GIL_TAG, "Imagen seleccionada")

                    lifecycleScope.launch(Dispatchers.IO) {

                        val updatePhoto = userRepository.updateProfile(
                            userProfile = userProfile,
                            userId = Utils.createPartFromString(userPreferences.getUserId().toString())
                        )

                        if (updatePhoto.isSuccessful){
                            val photo  = updatePhoto.body()
                            userPreferences.saveUserProfile(photo?.userProfile.toString())

                            withContext(Dispatchers.Main){
                                binding.ivProfile.setImageURI(uri)
                            }

                            DialogUtils.dismissLoadingDialog()
                            getActivityRootView()?.showSnackBar(getString(R.string.image_updated_success))
                        }else{
                            DialogUtils.dismissLoadingDialog()
                            getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                        }

                    }
                }else{
                    DialogUtils.dismissLoadingDialog()
                    getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
                }

            }else{
                //textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.greyDark_load))
                /*uriDB = "null"
                Log.d(Constants.GIL_TAG, "Imagen NO seleccionada")
                imageSelected.visibility = View.GONE
                imageSelected.setImageResource(R.drawable.ic_add_image)
                textAdd.setText(R.string.add_image)
                textAdd.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))*/
            }
        }


        binding.apply {
            etName.setText(userPreferences.getUserName().toString())
            etEmail.setText(userPreferences.getUserEmail().toString())
            Glide.with(requireActivity())
                .load(userPreferences.getUserProfile())
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        binding.ivProfile.setImageDrawable(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        binding.ivProfile.setImageResource(R.drawable.ic_no_photo)
                    }
                })
        }

        binding.ivEditPhoto.setOnClickListener { btnBack ->
            btnBack.applyClickAnimation()
            //binding.ivEditPhoto.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
            // binding.tvAddImage.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            //Tipo en especifico
            // pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("img/gif")))
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        binding.ivEditName.setOnClickListener{ btn ->
            btn.applyClickAnimation()
            Utils.showConfirmEditAlertDialog(
                requireContext(),
                getString(R.string.edit_name),
                binding.etName.text.toString(),
                getString(R.string.update),
                getString(R.string.cancel),
                onConfirm = {  newName ->
                    DialogUtils.showLoadingDialog(requireContext())
                    lifecycleScope.launch {
                        updateName(newName)
                    }
                }
            )
        }



        binding.btLogOut.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                userPreferences.clearAll()
                requireContext().deleteDatabase(Constants.DATABASE_NAME)
                val intent = Intent(requireContext(), LogInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }


    }

    private fun updateName(newName : String){
        if (Utils.isConnectedNow(requireContext())){
            lifecycleScope.launch(Dispatchers.IO) {

                val updateName = userRepository.updateName(
                    userId = userPreferences.getUserId().toString(),
                    userName = newName
                )

                if (updateName.isSuccessful){
                    val user  = updateName.body()
                    val useName = user?.name
                    userPreferences.saveUserName(useName.toString())

                    withContext(Dispatchers.Main){
                        binding.etName.setText(useName)
                    }

                    DialogUtils.dismissLoadingDialog()
                    getActivityRootView()?.showSnackBar(getString(R.string.edit_name_success))
                }else{
                    DialogUtils.dismissLoadingDialog()
                    getActivityRootView()?.showSnackBarError(getString(R.string.server_error))
                }

            }
        }else{
            DialogUtils.dismissLoadingDialog()
            getActivityRootView()?.showSnackBarError(getString(R.string.no_internet_connection))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}