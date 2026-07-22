package com.produktibo.launcher.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.produktibo.launcher.data.AppInfo
import com.produktibo.launcher.service.DoubleTapLockService
import com.produktibo.launcher.ui.theme.DarkSurface
import com.produktibo.launcher.ui.theme.OledBlack
import com.produktibo.launcher.ui.theme.TextMain
import com.produktibo.launcher.ui.theme.TextMuted

@Composable
fun SettingsScreen(
    context: Context,
    appList: List<AppInfo>,
    autoHideSocial: Boolean,
    autoHideGames: Boolean,
    doubleTapLockEnabled: Boolean,
    hiddenApps: Set<String>,
    onToggleSocialShield: (Boolean) -> Unit,
    onToggleGamesShield: (Boolean) -> Unit,
    onToggleDoubleTapLock: (Boolean) -> Unit,
    onToggleHideApp: (String) -> Unit,
    onRequestSetDefault: () -> Unit,
    onBack: () -> Unit
) {
    val isDefault = isDefaultLauncher(context)
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Top Navigation Bar (Standard Android Pattern: Back Button on Left)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Launcher",
                        tint = TextMain,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "PRODUKTIB O?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )
            }

            // Page Header Title & Subtitle
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = TextMain
            )
            Text(
                text = "Customize anti-doomscroll shield, games filter, gestures & visibility.",
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Section 1: Default Launcher Status & Reset
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "System Home Launcher",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextMain
                            )
                            Text(
                                text = if (isDefault) "Currently set as default launcher." else "Not set as default launcher.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                            )
                            Button(
                                onClick = onRequestSetDefault,
                                colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = OledBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isDefault) "Change / Reset Default Launcher" else "Set as Default Launcher",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Section 2: Master Social Media Shield Toggle
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Hide Social Media Apps",
                                    fontWeight = FontWeight.Medium,
                                    color = TextMain,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Automatically suppresses TikTok, X, Facebook, Threads, YouTube, Instagram, Snapchat & Reddit",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Switch(
                                checked = autoHideSocial,
                                onCheckedChange = onToggleSocialShield
                            )
                        }
                    }
                }

                // Section 3: Master Games Shield Toggle (NEW)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Hide Addictive Online Video Games",
                                    fontWeight = FontWeight.Medium,
                                    color = TextMain,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Automatically suppresses Mobile Legends, Roblox, PUBG, Genshin, CoD Mobile, Free Fire, and all Play Store game category apps",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Switch(
                                checked = autoHideGames,
                                onCheckedChange = onToggleGamesShield
                            )
                        }
                    }
                }

                // Section 4: Double-Tap to Lock Screen Toggle
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Double-Tap to Lock Screen",
                                    fontWeight = FontWeight.Medium,
                                    color = TextMain,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Double-tap empty space on home screen to turn off display",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Switch(
                                checked = doubleTapLockEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && DoubleTapLockService.instance == null) {
                                        showAccessibilityDialog = true
                                    }
                                    onToggleDoubleTapLock(enabled)
                                }
                            )
                        }
                    }
                }

                // Section 5: Custom App Visibility List Header
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CUSTOM APP VISIBILITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = TextMuted
                    )
                }

                // Individual Apps Visibility List
                items(appList, key = { it.packageName }) { app ->
                    val isHidden = hiddenApps.contains(app.packageName)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = app.label, color = TextMain, fontSize = 16.sp)
                            if (app.isSocialMedia) {
                                Text(text = "Social Doomscroll Tag", color = TextMuted, fontSize = 11.sp)
                            } else if (app.isGame) {
                                Text(text = "Game Category Tag", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                        Checkbox(
                            checked = !isHidden,
                            onCheckedChange = { onToggleHideApp(app.packageName) }
                        )
                    }
                }
            }
        }
    }

    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("Accessibility Permission Required", color = TextMain) },
            text = {
                Text(
                    "To enable double-tap screen lock, Android requires granting 'Produktib O? Screen Lock' permission in Accessibility Settings.",
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
