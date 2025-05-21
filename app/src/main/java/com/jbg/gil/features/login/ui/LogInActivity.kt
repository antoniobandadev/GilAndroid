package com.jbg.gil.features.login.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.jbg.gil.databinding.ActivityLoginBinding
import com.jbg.gil.features.home.ui.HomeActivity
import com.jbg.gil.core.utils.Constants
import com.jbg.gil.core.utils.Utils.getUserVals
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        screenSplash.setKeepOnScreenCondition {true}

        lifecycleScope.launch {
            val userPreferences = getUserVals(this@LogInActivity)
            withContext(Dispatchers.Main){
                if(userPreferences.isLogged){
                    Log.d(Constants.GIL_TAG, "Esta Loggeado")
                    val startIntentH = Intent(this@LogInActivity, HomeActivity::class.java)
                    startActivity(startIntentH)
                    finish()
                }else{
                    Log.d(Constants.GIL_TAG, "No esta Loggeado")
                    screenSplash.setKeepOnScreenCondition { false }
                }
            }
        }

    }
}