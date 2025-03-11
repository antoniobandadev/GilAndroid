package com.jbg.gil.ui.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jbg.gil.R
import com.jbg.gil.databinding.ActivityLoginBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fmLogActivity, LogInFragment.newInstance())
            .commit()


     /*   editTextListener()

        binding.btLogIn.setOnClickListener {
            val startIntentH = Intent(this,HomeActivity::class.java)
            validLogin(binding.etLogUser.text.toString().trim(), binding.etLogPass.text.toString().trim())
            //if(binding.etLogUser.text.toString() == "Antonio"){
            //    startActivity(startIntentH)
            //}else{
             //   binding.lbLogUser.error = "Error"
            //    Toast.makeText(this, getString(R.string.invalid_data), Toast.LENGTH_SHORT).show()
            //}
        }*/
    }








}