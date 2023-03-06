package com.serah.coparenting



import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseApp
import com.pusher.pushnotifications.PushNotifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val viewPager2=findViewById<ViewPager2>(R.id.view_pager)

        val toolbar=findViewById<MaterialToolbar>(R.id.tool_bar)

        setSupportActionBar(toolbar)

//        method to to trigger a redraw of the options menu:
        invalidateOptionsMenu()
        val tabsLayout=findViewById<TabLayout>(R.id.tab_layout)
        val viewPagerAdapter=ViewPagerAdapter(this,tabsLayout)
        viewPager2.adapter=viewPagerAdapter

        if(isInternetConnected()) {
            FirebaseApp.initializeApp(this);

            val userId = MyPreferences.getItemFromSP(applicationContext, "userId")
            PushNotifications.start(applicationContext, "d56028fe-3b37-4756-aa70-2ea42fc6ce56");
            PushNotifications.addDeviceInterest(userId);
        }else{
            Toast.makeText(applicationContext, "No internet connection!Make sure that you are connected to the internet.", Toast.LENGTH_LONG).show()
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int){
                tabsLayout.getTabAt(position)!!.select()
                super.onPageSelected(position)
            }
        })
        tabsLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.currentItem=tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
    private fun isInternetConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        invalidateOptionsMenu()
        menuInflater.inflate(R.menu.home_toolbar,menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.profile->startActivity(Intent(this@MainActivity,EditProfileActivity::class.java))
            R.id.logout-> {
                MyPreferences.removeItemFromSP(applicationContext,"userId")
                MyPreferences.removeItemFromSP(applicationContext,"token")
                startActivity(Intent(this@MainActivity,WelcomeActivity::class.java))
                finish()
//                lifecycleScope.launch {
//                    withContext(Dispatchers.IO) {
//                        testMpesa()
//                    }
//                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}

fun testMpesa() {

            val client = OkHttpClient().newBuilder().build()
            val mediaType = MediaType.parse("application/json")
            val requestBody = RequestBody.create(
                mediaType, """
    {
        "shortCode": 522522,
        "commandId": "CustomerPayBillOnline",
        "amount": "1",
        "msisdn": "254724084603",
        "billRefNumber": "1236509757"
    }
""".trimIndent()
            )
            val request = Request.Builder()
                .url("https://sandbox.safaricom.co.ke/mpesa/c2b/v1/simulate")
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer AmcDHznyn54HqYiCTdXQ9x2mEYuU")
                .build()
            val response = client.newCall(request).execute()

            Log.d("Test_response", response.toString())


}