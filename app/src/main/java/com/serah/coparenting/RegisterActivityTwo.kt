package com.serah.coparenting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater

import com.serah.coparenting.databinding.ActivityRegisterTwoBinding

class RegisterActivityTwo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityRegisterTwoBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)
        binding.continueBtn.setOnClickListener{
            startActivity(Intent(applicationContext,RegisterActivityThree::class.java))
        }
    }
}