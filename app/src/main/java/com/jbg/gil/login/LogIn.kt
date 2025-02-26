package com.jbg.gil.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jbg.gil.R
import com.jbg.gil.databinding.ActivityLoginBinding
import com.jbg.gil.home.HomeActivity

class LogIn : AppCompatActivity() {

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


        binding.btLogIn.setOnClickListener {
            val startIntentH = Intent(this,HomeActivity::class.java)

            if(binding.etLogUser.text.toString() == "Antonio"){
                startActivity(startIntentH)
            }else{
                //binding.etLogUser.error = "Error"
                binding.lbLogUser.helperText = "Error"
                binding.lbLogUser.error = "Error"
                Toast.makeText(this, getString(R.string.invalid_data), Toast.LENGTH_SHORT).show()
            }




        }


    }
}