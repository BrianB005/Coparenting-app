package com.serah.coparenting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater

import com.serah.coparenting.databinding.ActivityRegisterThreeBinding

class RegisterActivityThree : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterThreeBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.submitBtn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }
}