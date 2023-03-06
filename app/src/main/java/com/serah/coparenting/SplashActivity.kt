package com.serah.coparenting

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val token=MyPreferences.getItemFromSP(applicationContext,"token")!!
        val option=MyPreferences.getItemFromSP(applicationContext,"Option")!!
        Handler().postDelayed({
          if(token.isEmpty()){
              startActivity(Intent(applicationContext,WelcomeActivity::class.java))
          }else if(option == "YES"){
              startActivity(Intent(applicationContext,RegisterActivityTwo::class.java))
          }else{
              startActivity(Intent(applicationContext,MainActivity::class.java))
          }
            finish()
        },3000)

    }
}