package com.produktibo.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

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

    private val famousGamePackages = setOf(
        "com.mobile.legends",          // Mobile Legends: Bang Bang
        "com.roblox.client",           // Roblox
        "com.tencent.ig",              // PUBG Mobile
        "com.pubg.krmobile",           // PUBG Mobile KR
        "com.miHoYo.GenshinImpact",    // Genshin Impact
        "com.activision.callofduty.shooter", // Call of Duty Mobile
        "com.dts.freefireth",          // Free Fire
        "com.dts.freefiremax",         // Free Fire MAX
        "com.supercell.clashofclans",  // Clash of Clans
        "com.supercell.clashroyale",   // Clash Royale
        "com.supercell.brawlstars",    // Brawl Stars
        "com.king.candycrushsaga",     // Candy Crush Saga
        "com.scopely.monopolygo",      // Monopoly GO
        "com.nianticlabs.pokemongo",   // Pokemon GO
        "com.kiloo.subwaysurf",        // Subway Surfers
        "com.moonactive.coinmaster",   // Coin Master
        "com.levelfinite.hotta.gp",    // Tower of Fantasy
        "com.level5.yokai",            // Yo-kai Watch
        "com.riotgames.league.wildrift", // Wild Rift
        "com.garena.game.kgtw"         // Arena of Valor
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
                val appInfoObj = try {
                    pm.getApplicationInfo(pkgName, 0)
                } catch (e: Exception) {
                    null
                }

                val isCategoryGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appInfoObj != null) {
                    appInfoObj.category == ApplicationInfo.CATEGORY_GAME ||
                            (appInfoObj.flags and ApplicationInfo.FLAG_IS_GAME) != 0
                } else false

                val isGame = isCategoryGame || famousGamePackages.contains(pkgName) || isGameName(label)

                AppInfo(
                    label = label,
                    packageName = pkgName,
                    isSocialMedia = socialPackages.contains(pkgName) || isSocialName(label),
                    isGame = isGame,
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

    private fun isGameName(label: String): Boolean {
        val lower = label.lowercase()
        return lower.contains("game") || lower.contains("mobile legends") ||
                lower.contains("pubg") || lower.contains("roblox") ||
                lower.contains("genshin") || lower.contains("free fire") ||
                lower.contains("clash of") || lower.contains("wild rift") ||
                lower.contains("call of duty") || lower.contains("candy crush") ||
                lower.contains("subway surf") || lower.contains("brawl stars") ||
                lower.contains("monopoly") || lower.contains("casino") ||
                lower.contains("poker") || lower.contains("slots")
    }
}
