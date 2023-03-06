package com.serah.coparenting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

import com.serah.coparenting.databinding.ActivityRegisterBinding
import com.serah.coparenting.retrofit.AuthUser
import com.serah.coparenting.retrofit.AuthUserInterface
import com.serah.coparenting.retrofit.RetrofitHandler

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


        binding.registerBtn.setOnClickListener{
            val firstName=binding.inputFirstName.text.toString()
            val lastName=binding.inputLastName.text.toString()
            val email=binding.inputEmail.text.toString()
            val phone=binding.inputPhone.text.toString()
            val password=binding.inputPassword.text.toString()
            val confirmPassword=binding.confirmPassword.text.toString()

            if(password != confirmPassword){
                binding.textInputPassword2.error="Passwords do not match!"
            }
            if(firstName.isEmpty()){
                binding.firstName.error="Kindly fill this field"
            }
            if(lastName.isEmpty()){
                binding.lastName.error="Kindly fill this field"
            }
            if(email.isEmpty()){
                binding.textInputEmail.error="Kindly fill this field"
            }
            if(phone.isEmpty()){
                binding.textInputPhone.error="Kindly fill this field"
            }
            if(password.isEmpty()){
                binding.textInputPassword.error="Kindly fill this field"
            }
            if(confirmPassword.isEmpty()){
                binding.textInputPassword2.error="Kindly fill this field"
            }
            if(firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&confirmPassword.isNotEmpty() && password==confirmPassword && phone.isNotEmpty()){
                val userDetails=HashMap<String,String>()
                binding.registerBtn.text="Hold on ..."
                binding.registerBtn.isEnabled=false
//                binding.registerBtn.setAllowClickWhenDisabled(false)
                userDetails["firstName"] = firstName
                userDetails["lastName"] = lastName
                userDetails["email"] = email
                userDetails["phone"] = phone
                userDetails["password"] = password

                RetrofitHandler.register(userDetails,object :AuthUserInterface{
                    override fun success(user: AuthUser) {
                        MyPreferences.saveItemToSP(applicationContext,"token",user.token)
                        MyPreferences.saveItemToSP(applicationContext,"userId",user.user.userId)
                        val intent=Intent(applicationContext,RegisterActivityTwo::class.java)
                        startActivity(intent)
                        finish()
                    }
                    override fun failure(throwable: Throwable) {
                        Log.d("Exception",throwable.message!!)
                        binding.registerBtn.text="Register"
                        binding.registerBtn.isEnabled=true
                    }
                    override fun errorExists(message: String) {
                        Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                        binding.registerBtn.text="Register"
                        binding.registerBtn.isEnabled=true
                    }
                } )
            }
        }
        binding.openLogin.setOnClickListener{
            startActivity(Intent(applicationContext,LoginActivity::class.java))
            finish()
        }

    }
}