package com.serah.coparenting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2

import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager2=findViewById<ViewPager2>(R.id.view_pager)
        val tabsLayout=findViewById<TabLayout>(R.id.tab_layout)
        val viewPagerAdapter=ViewPagerAdapter(this,tabsLayout)
        viewPager2.adapter=viewPagerAdapter
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
}