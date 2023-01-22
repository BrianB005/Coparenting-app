package com.serah.coparenting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.serah.coparenting.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.openRegister.setOnClickListener{
            startActivity(Intent(applicationContext,RegisterActivity::class.java))
        }
        binding.loginBtn.setOnClickListener{
            startActivity(Intent(applicationContext,MainActivity::class.java))
        }
    }
}