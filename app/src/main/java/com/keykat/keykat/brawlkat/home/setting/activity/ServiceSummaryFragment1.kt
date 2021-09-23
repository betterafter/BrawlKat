package com.keykat.keykat.brawlkat.home.setting.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.keykat.keykat.brawlkat.R

class ServiceSummaryFragment1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.activity_settings_service_summary_fragment_1, container, false
        ) as ViewGroup
    }
}