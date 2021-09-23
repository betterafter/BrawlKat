package com.keykat.keykat.brawlkat.home.setting.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.keykat.keykat.brawlkat.databinding.ActivitySettingsServiceSummaryBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ServiceSummaryActivity : AppCompatActivity() {

    private var _settingsBinding: ActivitySettingsServiceSummaryBinding? = null
    private val binding get() = _settingsBinding!!

    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _settingsBinding = ActivitySettingsServiceSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.serviceSummaryPager

        pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        dotsIndicator = binding.dotsIndicator
        dotsIndicator.setViewPager2(viewPager)
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        private val fragmentCount = 3
        private val fragment = listOf(
            ServiceSummaryFragment1(),
            ServiceSummaryFragment2(),
            ServiceSummaryFragment3()
        )

        override fun getItemCount(): Int = fragmentCount

        override fun createFragment(position: Int): Fragment = fragment[position]
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }
}