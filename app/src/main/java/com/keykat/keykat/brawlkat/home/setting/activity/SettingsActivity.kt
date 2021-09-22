package com.keykat.keykat.brawlkat.home.setting.activity

import android.Manifest
import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.keykat.keykat.brawlkat.R
import com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private var foregroundServicePreferences: SwitchPreferenceCompat? = null
        private var serviceEditTextPreference: EditTextPreference? = null
        private var policyEditTextPreference: EditTextPreference? = null
        private var developerInfoEditTextPreference: EditTextPreference? = null

        private lateinit var serviceIntent: Intent

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            serviceIntent = Intent(
                requireActivity().applicationContext,
                kat_Service_BrawlStarsNotifActivity::class.java
            )

            foregroundServicePreferences = findPreference(getString(R.string.notify_service))
            serviceEditTextPreference = findPreference(getString(R.string.service))
            policyEditTextPreference = findPreference(getString(R.string.policy))
            developerInfoEditTextPreference = findPreference(getString(R.string.developer))

            foregroundServicePreferences?.setOnPreferenceChangeListener { _, newValue ->
                if (!checkPermission())
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))

                if (newValue.equals(true)) {
                    requireActivity().startForegroundService(serviceIntent)
                } else {
                    requireActivity().stopService(serviceIntent)
                }

                true
            }
        }

        private fun checkPermission(): Boolean {
            val granted: Boolean
            val appOps = activity?.getSystemService(APP_OPS_SERVICE) as AppOpsManager
            val mode = activity?.applicationContext?.packageName?.let {
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(), it
                )
            }
            granted = if (mode == AppOpsManager.MODE_DEFAULT) {
                activity?.checkCallingOrSelfPermission(
                    Manifest.permission.PACKAGE_USAGE_STATS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                mode == AppOpsManager.MODE_ALLOWED
            }
            return granted
        }
    }
}