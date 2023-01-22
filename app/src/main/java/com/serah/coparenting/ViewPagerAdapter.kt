package com.serah.coparenting

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout


class ViewPagerAdapter(fragmentActivity: MainActivity, private val tabLayout: TabLayout) :
    FragmentStateAdapter(fragmentActivity) {




    override fun getItemCount(): Int {
        return tabLayout.tabCount
    }

    override fun createFragment(position: Int): Fragment {
        val selectedFragment=when (position){
            0->ChatFragment()
            1->CalendarFragment()
            2->GalleryFragment()
            3->ExpensesFragment()
            else->ChatFragment()
        }
        return selectedFragment

    }
}