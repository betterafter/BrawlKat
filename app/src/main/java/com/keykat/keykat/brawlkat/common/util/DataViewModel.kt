package com.keykat.keykat.brawlkat.common.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel : ViewModel() {

    private val _isBackgroundServiceOn = MutableLiveData<Boolean>()
    val isBackgroundServiceOn: LiveData<Boolean> = _isBackgroundServiceOn
}