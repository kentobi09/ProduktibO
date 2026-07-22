package com.produktibo.launcher.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.produktibo.launcher.data.PreferencesManager
import com.produktibo.launcher.ui.LockScreenActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ScreenStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_SCREEN_OFF || action == Intent.ACTION_SCREEN_ON) {
            val prefsManager = PreferencesManager(context)
            CoroutineScope(Dispatchers.IO).launch {
                val isEnabled = prefsManager.minimalLockscreenEnabled.first()
                if (isEnabled) {
                    val lockIntent = Intent(context, LockScreenActivity::class.java).apply {
                        addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                        )
                    }
                    context.startActivity(lockIntent)
                }
            }
        }
    }
}
