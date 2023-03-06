package com.serah.coparenting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.serah.coparenting.databinding.ActivityLoginBinding
import com.serah.coparenting.retrofit.AuthUser
import com.serah.coparenting.retrofit.AuthUserInterface
import com.serah.coparenting.retrofit.RetrofitHandler

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.openRegister.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.inputEmail.text.toString()

            val password = binding.inputPassword.text.toString()
            if (email.isEmpty()) {
                binding.textInputEmail.error = "Kindly fill this field"
            }
            if (password.isEmpty()) {
                binding.textInputPassword.error = "Kindly fill this field"
            }
            if(password.isNotEmpty() && email.isNotEmpty()){
                val userDetails=HashMap<String,String>()
                userDetails["email"]=email
                userDetails["password"]=password
                binding.loginBtn.text="Hold on ..."

                binding.loginBtn.isEnabled=false
                RetrofitHandler.login(userDetails,object:AuthUserInterface{
                    override fun success(user: AuthUser) {
                        Toast.makeText(applicationContext,"Welcome back ${user.user.firstName}",Toast.LENGTH_LONG).show()
                        MyPreferences.saveItemToSP(applicationContext,"token",user.token)
                        MyPreferences.saveItemToSP(applicationContext,"userId",user.user.userId)
                        Thread.sleep(2000)
                        startActivity(Intent(applicationContext,MainActivity::class.java))
                        finish()
                    }
                    override fun failure(throwable: Throwable) {
                        Log.d("Exception",throwable.message!!)
                        binding.loginBtn.text="Login"
                        binding.loginBtn.isEnabled=true
                    }
                    override fun errorExists(message: String) {
                        binding.loginBtn.text="Login"
                        binding.loginBtn.isEnabled=true
                        Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()

                    }

                })
            }

        }

    }
}