package com.keykat.keykat.brawlkat.home.setting.activity

import android.os.Bundle
import android.text.util.Linkify
import androidx.appcompat.app.AppCompatActivity
import com.keykat.keykat.brawlkat.R
import com.keykat.keykat.brawlkat.databinding.ActivitySettingsPolicySummaryBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

class PolicySummaryActivity : AppCompatActivity() {

    private var _policySummaryBinding: ActivitySettingsPolicySummaryBinding? = null
    private val binding get() = _policySummaryBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _policySummaryBinding = ActivitySettingsPolicySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val filter =
            Linkify.TransformFilter(object : Linkify.TransformFilter, (Matcher, String) -> String {
                override fun transformUrl(p0: Matcher?, p1: String?): String {
                    return ""
                }

                override fun invoke(p1: Matcher, p2: String): String {
                    return ""
                }
            })

        val pattern = Pattern.compile(getString(R.string.policy_link_pattern))
        Linkify.addLinks(
            binding.fanContentPolicyText,
            pattern,
            getString(R.string.policy_link),
            null,
            filter
        )

        val koreanPattern = Pattern.compile(getString(R.string.policy_link_pattern_korean))
        Linkify.addLinks(
            binding.fanContentPolicyTextKorean,
            koreanPattern,
            getString(R.string.policy_link_korean),
            null,
            filter
        )
    }
}