package com.serah.coparenting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.Toast

import com.serah.coparenting.databinding.ActivityRegisterTwoBinding
import com.serah.coparenting.retrofit.ResponseBodyInterface
import com.serah.coparenting.retrofit.RetrofitHandler
import okhttp3.ResponseBody

class RegisterActivityTwo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityRegisterTwoBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)
//        checking selected option
        binding.options.setOnCheckedChangeListener { _, i ->
            val checkedOption=findViewById<RadioButton>(i);
            if(checkedOption.text.equals("YES")){
                MyPreferences.saveItemToSP(applicationContext,"Option","YES")
                binding.textInputCode.visibility= View.VISIBLE;
                binding.continueBtn.visibility=View.VISIBLE;
                binding.continueBtn.setOnClickListener{
                    val userId=binding.inputCode.text.toString()
                    if(userId.isEmpty()){
                        binding.textInputCode.error="Kindly fill this field!"
                    }else{

                        RetrofitHandler.addCoparent(applicationContext,userId,object:ResponseBodyInterface{
                            override fun success(response: ResponseBody) {
                                Toast.makeText(applicationContext,"Co-parent added successfully!",Toast.LENGTH_LONG).show()
                                MyPreferences.removeItemFromSP(applicationContext,"Option")
                                startActivity(Intent(this@RegisterActivityTwo,MainActivity::class.java))
                                finish()
                            }
                            override fun failure(throwable: Throwable) {
                                Toast.makeText(applicationContext,throwable.message,Toast.LENGTH_LONG).show()
                            }

                            override fun errorExists(message: String) {
                                Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                            }

                        })
                    }
                }
            }else{
                MyPreferences.removeItemFromSP(applicationContext,"Option")
                startActivity(Intent(this@RegisterActivityTwo,MainActivity::class.java))
                finish()
            }
        }
    }
}