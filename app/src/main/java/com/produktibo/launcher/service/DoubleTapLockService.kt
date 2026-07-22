package com.produktibo.launcher.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent

class DoubleTapLockService : AccessibilityService() {

    private var screenStateReceiver: ScreenStateReceiver? = null

    companion object {
        var instance: DoubleTapLockService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        registerScreenStateReceiver()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        unregisterScreenStateReceiver()
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed for lock action
    }

    override fun onInterrupt() {
        instance = null
    }

    fun lockScreen() {
        try {
            performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerScreenStateReceiver() {
        try {
            if (screenStateReceiver == null) {
                screenStateReceiver = ScreenStateReceiver()
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_OFF)
                    addAction(Intent.ACTION_SCREEN_ON)
                }
                registerReceiver(screenStateReceiver, filter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun unregisterScreenStateReceiver() {
        screenStateReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                // Ignore
            }
            screenStateReceiver = null
        }
    }
}
