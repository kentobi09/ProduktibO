package com.produktibo.launcher.ui

import android.app.KeyguardManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.produktibo.launcher.service.DoubleTapLockService
import com.produktibo.launcher.service.PlainNotificationService
import com.produktibo.launcher.ui.theme.DarkSurface
import com.produktibo.launcher.ui.theme.OledBlack
import com.produktibo.launcher.ui.theme.ProduktibOTheme
import com.produktibo.launcher.ui.theme.TextMain
import com.produktibo.launcher.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.*

class LockScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupShowWhenLocked()

        setContent {
            ProduktibOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = OledBlack
                ) {
                    MinimalLockScreenContent(
                        context = this,
                        onUnlock = { unlockPhone() }
                    )
                }
            }
        }
    }

    private fun setupShowWhenLocked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun unlockPhone() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && keyguardManager != null) {
            keyguardManager.requestDismissKeyguard(this, null)
        }
        finish()
    }
}

@Composable
fun MinimalLockScreenContent(
    context: Context,
    onUnlock: () -> Unit
) {
    var currentTime by remember { mutableStateOf(getFormattedTime()) }
    var currentDate by remember { mutableStateOf(getFormattedDate()) }
    var batteryPercentage by remember { mutableStateOf(getBatteryLevel(context)) }

    val notifications by PlainNotificationService.notifications.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedTime()
            currentDate = getFormattedDate()
            batteryPercentage = getBatteryLevel(context)
            kotlinx.coroutines.delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Double tap empty space on lock screen turns off display natively
                        DoubleTapLockService.instance?.lockScreen()
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) { // Swipe Up to Unlock
                        onUnlock()
                    }
                }
            }
            .padding(horizontal = 24.dp, vertical = 36.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PRODUKTIB O?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )
                Text(
                    text = "$batteryPercentage%",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Large Minimalist Clock
            Text(
                text = currentTime,
                fontSize = 72.sp,
                fontWeight = FontWeight.ExtraLight,
                color = TextMain
            )
            Text(
                text = currentDate,
                fontSize = 16.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Quiet Notifications List
            if (notifications.isNotEmpty()) {
                Text(
                    text = "NOTIFICATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(notifications.take(5), key = { it.id }) { notif ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurface, RoundedCornerShape(8.dp))
                                .border(1.dp, TextMuted, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = notif.appName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            if (notif.title.isNotEmpty()) {
                                Text(
                                    text = notif.title,
                                    fontSize = 13.sp,
                                    color = TextMain,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            if (notif.text.isNotEmpty()) {
                                Text(
                                    text = notif.text,
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Swipe Up Prompt at Bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "▲ Swipe up to unlock",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
            }
        }
    }
}

private fun getBatteryLevel(context: Context): Int {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    return batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 100
}

private fun getFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

private fun getFormattedDate(): String {
    val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    return sdf.format(Date())
}
