package com.produktibo.launcher.data

import android.content.Intent

data class AppInfo(
    val label: String,
    val packageName: String,
    val isSocialMedia: Boolean = false,
    val isHidden: Boolean = false,
    val dailyLimitMinutes: Int? = null,
    val timeUsedTodaySeconds: Long = 0,
    val isFrozen: Boolean = false,
    val intent: Intent? = null
)
