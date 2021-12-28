package com.keykat.keykat.brawlkat.common.util

import android.content.Context

fun Context.getAppName(): String = applicationInfo.loadLabel(packageManager).toString()