package com.serah.coparenting

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer

import com.google.android.material.tabs.TabLayoutMediator
import com.serah.coparenting.databinding.ActivityWelcomeBinding


class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityWelcomeBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.openLogin.setOnClickListener{
            startActivity(Intent(applicationContext,LoginActivity::class.java))
        }
        binding.openRegister.setOnClickListener{
            startActivity(Intent(applicationContext,RegisterActivity::class.java))
        }

        val viewPager=binding.viewPager
        val tabLayout=binding.tabLayout
        viewPager.apply {
            clipChildren = false
            clipToPadding = false
            offscreenPageLimit = 3
            (getChildAt(0) as RecyclerView).overScrollMode =
                RecyclerView.OVER_SCROLL_ALWAYS
        }

        val carouselData= arrayListOf(
            CarouselItem("Smart calendar for effective planning","Work from a shared calendar.Create easy to follow schedules and manage smooth custody shifts",R.drawable.calendar),
            CarouselItem("Secure,instant and credible Messaging"," Use instant messaging platform for timely communications.Messages can neither be deleted nor edited",R.drawable.message),
                    CarouselItem("Detailed Expense Log "," Track your parenting expenses and reimbursements.Share expenses and keep track of all expenses",R.drawable.expenses),
        CarouselItem("Memories bank","Upload photos and or videos of moments worth recalling.",R.drawable.gallery)
        )
        val carouselAdapter=CarouselAdapter(carouselData)
        viewPager.adapter=carouselAdapter



        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer((40 * Resources.getSystem().displayMetrics.density).toInt()))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = (0.80f + r * 0.20f)
        }
        viewPager.setPageTransformer(compositePageTransformer)

//        adding tablayout to indicate carousel item selection
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

        }.attach()
    }
}