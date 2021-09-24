package com.keykat.keykat.brawlkat.util

import android.content.Context

fun Context.getAppName(): String = applicationInfo.loadLabel(packageManager).toString()