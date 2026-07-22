package com.produktibo.launcher.service

import android.content.Intent
import android.content.IntentFilter
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PlainNotification(
    val id: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class PlainNotificationService : NotificationListenerService() {

    private var screenStateReceiver: ScreenStateReceiver? = null

    companion object {
        private val _notifications = MutableStateFlow<List<PlainNotification>>(emptyList())
        val notifications: StateFlow<List<PlainNotification>> = _notifications

        var instance: PlainNotificationService? = null
            private set

        fun clearAll() {
            instance?.let { service ->
                try {
                    service.cancelAllNotifications()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            _notifications.value = emptyList()
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        updateNotifications()
        registerScreenStateReceiver()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        instance = null
        unregisterScreenStateReceiver()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        updateNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        updateNotifications()
    }

    private fun updateNotifications() {
        try {
            val activeNotifs = activeNotifications ?: return
            val list = activeNotifs.mapNotNull { sbn ->
                val extras = sbn.notification.extras
                val title = extras.getCharSequence("android.title")?.toString() ?: ""
                val text = extras.getCharSequence("android.text")?.toString() ?: ""
                val pkg = sbn.packageName

                if (title.isEmpty() && text.isEmpty()) return@mapNotNull null
                if (pkg == packageName) return@mapNotNull null

                val appName = try {
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(pkg, 0)
                    ).toString()
                } catch (e: Exception) {
                    pkg
                }

                PlainNotification(
                    id = "${sbn.key}_${sbn.postTime}",
                    packageName = pkg,
                    appName = appName,
                    title = title,
                    text = text,
                    timestamp = sbn.postTime
                )
            }.sortedByDescending { it.timestamp }

            _notifications.value = list
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
