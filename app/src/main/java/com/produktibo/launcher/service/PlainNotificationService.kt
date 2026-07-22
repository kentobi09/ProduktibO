package com.produktibo.launcher.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PlainNotification(
    val id: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val timestamp: Long
)

class PlainNotificationService : NotificationListenerService() {

    companion object {
        var instance: PlainNotificationService? = null
            private set

        private val _notifications = MutableStateFlow<List<PlainNotification>>(emptyList())
        val notifications: StateFlow<List<PlainNotification>> = _notifications.asStateFlow()

        fun clearAll() {
            _notifications.value = emptyList()
            instance?.cancelAllNotifications()
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        updateNotifications()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        updateNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        updateNotifications()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    private fun updateNotifications() {
        try {
            val active = activeNotifications ?: return
            val pm = packageManager
            val list = active.mapNotNull { sbn ->
                val extras = sbn.notification.extras
                val title = extras.getString("android.title") ?: ""
                val text = extras.getCharSequence("android.text")?.toString() ?: ""
                val pkgName = sbn.packageName

                if (title.isNotEmpty() || text.isNotEmpty()) {
                    val appLabel = try {
                        val appInfo = pm.getApplicationInfo(pkgName, 0)
                        pm.getApplicationLabel(appInfo).toString()
                    } catch (e: Exception) {
                        pkgName
                    }

                    PlainNotification(
                        id = "${pkgName}_${sbn.id}_${sbn.postTime}",
                        packageName = pkgName,
                        appName = appLabel,
                        title = title,
                        text = text,
                        timestamp = sbn.postTime
                    )
                } else null
            }.sortedByDescending { it.timestamp }

            _notifications.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
