package com.produktibo.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class AppRepository(private val context: Context) {

    private val socialPackages = setOf(
        "com.zhiliaoapp.musically", // TikTok
        "com.ss.android.ugc.trill", // TikTok Asia
        "com.twitter.android",       // X / Twitter
        "com.facebook.katana",      // Facebook
        "com.instagram.android",    // Instagram
        "com.instagram.barcelona",  // Threads
        "com.google.android.youtube", // YouTube
        "com.reddit.frontpage",     // Reddit
        "com.snapchat.android"      // Snapchat
    )

    fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        return resolveInfos.mapNotNull { info ->
            val pkgName = info.activityInfo.packageName
            val label = info.loadLabel(pm).toString()
            val launchIntent = pm.getLaunchIntentForPackage(pkgName)

            if (launchIntent != null && pkgName != context.packageName) {
                AppInfo(
                    label = label,
                    packageName = pkgName,
                    isSocialMedia = socialPackages.contains(pkgName) || isSocialName(label),
                    intent = launchIntent
                )
            } else null
        }.sortedBy { it.label.lowercase() }
    }

    private fun isSocialName(label: String): Boolean {
        val lower = label.lowercase()
        return lower.contains("tiktok") || lower.contains("twitter") ||
                lower.contains("facebook") || lower.contains("instagram") ||
                lower.contains("threads") || lower.contains("youtube") ||
                lower.contains("reddit") || lower.contains("snapchat")
    }
}
