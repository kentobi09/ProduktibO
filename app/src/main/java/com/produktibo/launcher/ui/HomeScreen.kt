package com.produktibo.launcher.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.produktibo.launcher.MainActivity
import com.produktibo.launcher.data.AppInfo
import com.produktibo.launcher.service.DoubleTapLockService
import com.produktibo.launcher.service.PlainNotification
import com.produktibo.launcher.service.PlainNotificationService
import com.produktibo.launcher.ui.theme.DarkSurface
import com.produktibo.launcher.ui.theme.OledBlack
import com.produktibo.launcher.ui.theme.TextMain
import com.produktibo.launcher.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.*

fun isDefaultLauncher(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
    val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfo?.activityInfo?.packageName == context.packageName
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    context: Context,
    appList: List<AppInfo>,
    autoHideSocial: Boolean,
    autoHideGames: Boolean,
    doubleTapLockEnabled: Boolean,
    hiddenApps: Set<String>,
    onOpenSettings: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentTime by remember { mutableStateOf(getFormattedTime()) }
    var currentDate by remember { mutableStateOf(getFormattedDate()) }
    var batteryPercentage by remember { mutableStateOf(getBatteryLevel(context)) }
    var showNotifSheet by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    val notifications by PlainNotificationService.notifications.collectAsState()

    // Live Ticker for Time & Battery Level
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedTime()
            currentDate = getFormattedDate()
            batteryPercentage = getBatteryLevel(context)
            kotlinx.coroutines.delay(2000)
        }
    }

    val filteredApps = remember(appList, searchQuery, autoHideSocial, autoHideGames, hiddenApps) {
        appList.filter { app ->
            val isHiddenBySocialShield = autoHideSocial && app.isSocialMedia
            val isHiddenByGamesShield = autoHideGames && app.isGame
            val isCustomHidden = hiddenApps.contains(app.packageName)
            val matchesSearch = searchQuery.isEmpty() || app.label.contains(searchQuery, ignoreCase = true)

            !isHiddenBySocialShield && !isHiddenByGamesShield && !isCustomHidden && matchesSearch
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
            .pointerInput(doubleTapLockEnabled) {
                detectTapGestures(
                    onDoubleTap = {
                        if (doubleTapLockEnabled) {
                            val lockService = DoubleTapLockService.instance
                            if (lockService != null) {
                                lockService.lockScreen()
                            } else {
                                showAccessibilityDialog = true
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount > 15) {
                        MainActivity.collapseSystemStatusBar(context)
                        showNotifSheet = true
                    } else if (dragAmount < -15) {
                        showNotifSheet = false
                    }
                }
            }
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Plain Minimal Status Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
            }

            // Minimal Header Clock & Date
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = currentTime,
                fontSize = 58.sp,
                fontWeight = FontWeight.Light,
                color = TextMain
            )
            Text(
                text = currentDate,
                fontSize = 16.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "\"Produktib O? It's productive — make every minute count.\"",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Instant Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search apps...", color = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextMain,
                    unfocusedBorderColor = TextMuted
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App Drawer Text List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "APPLICATIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                TextButton(onClick = onOpenSettings) {
                    Text("Settings", color = TextMain, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No apps visible. (Check hidden settings)",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        Text(
                            text = app.label,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextMain,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        app.intent?.let { intent ->
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Top-to-Bottom Pull-Down Notification Shade Animation
        AnimatedVisibility(
            visible = showNotifSheet,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(16.dp)
                    .border(1.dp, TextMuted, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextMain
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (notifications.isNotEmpty()) {
                                TextButton(onClick = { PlainNotificationService.clearAll() }) {
                                    Text("Clear All", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                            TextButton(onClick = { showNotifSheet = false }) {
                                Text("▲ Close", color = TextMain, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (PlainNotificationService.instance == null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = OledBlack),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Notification Permission Required",
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Grant notification listener access to convert noisy alerts into quiet text.",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                                Button(
                                    onClick = {
                                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = OledBlack)
                                ) {
                                    Text("Grant Permission", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    } else if (notifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No active notifications. Stay focused!",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 340.dp)
                        ) {
                            items(notifications, key = { it.id }) { notif ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(OledBlack, RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = notif.appName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted
                                        )
                                        Text(
                                            text = getRelativeTimeString(notif.timestamp),
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                    if (notif.title.isNotEmpty()) {
                                        Text(
                                            text = notif.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
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
                    }
                }
            }
        }
    }

    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("Screen Lock Permission Required", color = TextMain) },
            text = {
                Text(
                    "To allow double-tapping to turn off your phone screen, Android requires enabling 'Produktib O? Screen Lock' once in Accessibility Settings.",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAccessibilityDialog = false
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = OledBlack)
                ) {
                    Text("Open Settings", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccessibilityDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }
}

private fun getRelativeTimeString(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val mins = diff / (1000 * 60)
    return when {
        mins < 1 -> "Just now"
        mins < 60 -> "${mins}m ago"
        else -> "${mins / 60}h ago"
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
