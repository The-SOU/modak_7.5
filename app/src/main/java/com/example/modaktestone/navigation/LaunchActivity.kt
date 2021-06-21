package com.example.modaktestone.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.modaktestone.LoginActivity
import com.example.modaktestone.MainActivity
import com.example.modaktestone.R
import com.example.modaktestone.databinding.ActivityLaunchBinding
import com.example.modaktestone.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseUser

class LaunchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.launchBtn.setOnClickListener {
            startActivity(Intent(this, PhoneCertificationActivity::class.java))
            finish()
        }

        binding.launchBtnAdminister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    fun moveMainpage(user: FirebaseUser?) {
        if(user != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    //어스에 커렌트유저가 있으면 바로메인페이지
//    override fun onStart() {
//        super.onStart()
//        moveMainpage(auth?.currentUser)
//    }
}