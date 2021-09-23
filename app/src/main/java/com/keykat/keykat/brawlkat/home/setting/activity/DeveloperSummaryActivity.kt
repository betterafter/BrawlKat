package com.keykat.keykat.brawlkat.home.setting.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.keykat.keykat.brawlkat.databinding.ActivitySettingsDeveloperSummaryBinding

class DeveloperSummaryActivity : AppCompatActivity() {

    private var _developerSummaryBinding: ActivitySettingsDeveloperSummaryBinding? = null
    private val binding get() = _developerSummaryBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _developerSummaryBinding = ActivitySettingsDeveloperSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}