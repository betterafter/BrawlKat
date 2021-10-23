package com.keykat.keykat.brawlkat.common.model.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.keykat.keykat.brawlkat.R
import com.keykat.keykat.brawlkat.common.SharedPreferenceKey

class SharedPreferenceManager(
    private val context: Context
) {

    private val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.sharedPreferenceKey), Context.MODE_PRIVATE
    )

    fun getPreference(): SharedPreferences {
        return sharedPreferences;
    }

    @SuppressLint("CommitPrefEdits")
    fun putAccount(tag: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(SharedPreferenceKey.ACCOUNT_PREFERENCE_KEY.key, tag).apply()
    }

    fun getAccount(): String? {
        return sharedPreferences.getString(SharedPreferenceKey.ACCOUNT_PREFERENCE_KEY.key, "")
    }

}