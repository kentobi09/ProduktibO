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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    hasCompletedOnboarding: Boolean,
    hiddenApps: Set<String>,
    onSaveOnboardingSelection: (Set<String>) -> Unit,
    onOpenSettings: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentTime by remember { mutableStateOf(getFormattedTime()) }
    var currentDate by remember { mutableStateOf(getFormattedDate()) }
    var batteryPercentage by remember { mutableStateOf(getBatteryLevel(context)) }
    var showNotifSheet by remember { mutableStateOf(false) }
    var showAccessibilityDialog by remember { mutableStateOf(false) }
    var activeLetterIndicator by remember { mutableStateOf<Char?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val notifications by PlainNotificationService.notifications.collectAsState()

    // Auto-fade letter indicator after 800ms
    LaunchedEffect(activeLetterIndicator) {
        if (activeLetterIndicator != null) {
            delay(800)
            activeLetterIndicator = null
        }
    }

    // Live Ticker for Time & Battery Level
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedTime()
            currentDate = getFormattedDate()
            batteryPercentage = getBatteryLevel(context)
            delay(2000)
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

    // A-Z Alphabet Scroll Letter Map (letter -> first item index in filteredApps)
    val alphabetIndexMap = remember(filteredApps) {
        val map = mutableMapOf<Char, Int>()
        filteredApps.forEachIndexed { index, app ->
            val firstChar = app.label.firstOrNull()?.uppercaseChar() ?: '#'
            if (firstChar.isLetter() && !map.containsKey(firstChar)) {
                map[firstChar] = index
            }
        }
        map
    }

    val alphabetLetters = remember { ('A'..'Z').toList() }

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
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 15) {
                        MainActivity.collapseSystemStatusBar(context)
                        showNotifSheet = true
                    } else if (dragAmount < -15) {
                        showNotifSheet = false
                    }
                }
            }
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Plain Minimal Status Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
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

            // Compact Clock & Compact Tagline
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentTime,
                fontSize = 54.sp,
                fontWeight = FontWeight.Light,
                color = TextMain
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDate,
                    fontSize = 14.sp,
                    color = TextMuted
                )
                Text(
                    text = "It's productive",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ultra-Thin Space-Saving Single Line Search Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = TextStyle(color = TextMain, fontSize = 15.sp),
                    cursorBrush = SolidColor(TextMain),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text("Search apps...", color = TextMuted, fontSize = 15.sp)
                            }
                            innerTextField()
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(TextMuted)
                        .align(Alignment.BottomCenter)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Drawer Text List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "APPLICATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                TextButton(onClick = onOpenSettings, contentPadding = PaddingValues(0.dp)) {
                    Text("Settings", color = TextMain, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

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
                // Layout with Main App List + Right A-Z Alphabet Scroll Bar
                Row(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        itemsIndexed(filteredApps, key = { _, app -> app.packageName }) { _, app ->
                            Text(
                                text = app.label,
                                fontSize = 20.sp,
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
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }

                    // A-Z Side Alphabet Fast-Scroll Bar
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 8.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        alphabetLetters.forEach { letter ->
                            val isAvailable = alphabetIndexMap.containsKey(letter)
                            Text(
                                text = letter.toString(),
                                fontSize = 9.sp,
                                fontWeight = if (isAvailable) FontWeight.Bold else FontWeight.Normal,
                                color = if (isAvailable) TextMain else TextMuted.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .clickable(enabled = isAvailable) {
                                        alphabetIndexMap[letter]?.let { targetIndex ->
                                            activeLetterIndicator = letter
                                            coroutineScope.launch {
                                                listState.scrollToItem(targetIndex)
                                            }
                                        }
                                    }
                                    .padding(vertical = 1.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Center Floating Fading Letter Indicator (A-Z Scroll Indicator)
        AnimatedVisibility(
            visible = activeLetterIndicator != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                color = DarkSurface.copy(alpha = 0.92f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .size(90.dp)
                    .border(1.dp, TextMuted, RoundedCornerShape(20.dp))
                    .shadow(12.dp, RoundedCornerShape(20.dp))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = activeLetterIndicator?.toString() ?: "",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
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
                            itemsIndexed(notifications, key = { _, notif -> notif.id }) { _, notif ->
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

    // First-Install App Selection Onboarding Dialog
    if (!hasCompletedOnboarding) {
        OnboardingAppSelectionDialog(
            appList = appList,
            initialHiddenApps = hiddenApps,
            onConfirm = { selectedVisibleApps ->
                val allPackageNames = appList.map { it.packageName }.toSet()
                val hiddenSet = allPackageNames - selectedVisibleApps
                onSaveOnboardingSelection(hiddenSet)
            }
        )
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

@Composable
fun OnboardingAppSelectionDialog(
    appList: List<AppInfo>,
    initialHiddenApps: Set<String>,
    onConfirm: (Set<String>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedPackages = remember(appList, initialHiddenApps) {
        mutableStateListOf<String>().apply {
            val visibleInitial = appList.map { it.packageName }.filter { !initialHiddenApps.contains(it) }
            addAll(visibleInitial)
        }
    }

    val filteredApps = remember(appList, searchQuery) {
        if (searchQuery.isEmpty()) appList else appList.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = { /* Force user selection once on first install */ },
        title = {
            Column {
                Text(text = "Choose Apps to Show", fontWeight = FontWeight.Bold, color = TextMain, fontSize = 18.sp)
                Text(
                    text = "Select which apps to show in your minimalist launcher drawer.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search apps...", color = TextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextMain,
                        unfocusedBorderColor = TextMuted
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            selectedPackages.clear()
                            selectedPackages.addAll(appList.map { it.packageName })
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Select All", fontSize = 11.sp, color = TextMain)
                    }
                    TextButton(
                        onClick = { selectedPackages.clear() },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Deselect All", fontSize = 11.sp, color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    itemsIndexed(filteredApps, key = { _, app -> app.packageName }) { _, app ->
                        val isSelected = selectedPackages.contains(app.packageName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) selectedPackages.remove(app.packageName) else selectedPackages.add(app.packageName)
                                }
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = app.label, color = TextMain, fontSize = 14.sp)
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) selectedPackages.add(app.packageName) else selectedPackages.remove(app.packageName)
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedPackages.toSet()) },
                colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = OledBlack)
            ) {
                Text("Save & Continue", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkSurface
    )
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
