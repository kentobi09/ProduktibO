package com.produktibo.launcher

import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.produktibo.launcher.data.AppInfo
import com.produktibo.launcher.data.AppRepository
import com.produktibo.launcher.data.PreferencesManager
import com.produktibo.launcher.ui.HomeScreen
import com.produktibo.launcher.ui.SettingsScreen
import com.produktibo.launcher.ui.isDefaultLauncher
import com.produktibo.launcher.ui.theme.ProduktibOTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var appRepository: AppRepository
    private lateinit var prefsManager: PreferencesManager

    private val defaultHomeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Callback after returning from system launcher chooser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Total Immersive Full Screen: Suppress & Hide System Status and Navigation Bars
        setupTotalImmersiveFullScreen()

        appRepository = AppRepository(this)
        prefsManager = PreferencesManager(this)

        setContent {
            ProduktibOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    var isSettingsOpen by remember { mutableStateOf(false) }
                    var appList by remember { mutableStateOf<List<AppInfo>>(emptyList()) }

                    val autoHideSocial by prefsManager.autoHideSocial.collectAsState(initial = true)
                    val autoHideGames by prefsManager.autoHideGames.collectAsState(initial = true)
                    val doubleTapLockEnabled by prefsManager.doubleTapLockEnabled.collectAsState(initial = false)
                    val hiddenApps by prefsManager.hiddenAppsSet.collectAsState(initial = emptySet())

                    LaunchedEffect(Unit) {
                        appList = appRepository.getInstalledApps()
                    }

                    if (isSettingsOpen) {
                        SettingsScreen(
                            context = this,
                            appList = appList,
                            autoHideSocial = autoHideSocial,
                            autoHideGames = autoHideGames,
                            doubleTapLockEnabled = doubleTapLockEnabled,
                            hiddenApps = hiddenApps,
                            onToggleSocialShield = { enabled ->
                                lifecycleScope.launch { prefsManager.setAutoHideSocial(enabled) }
                            },
                            onToggleGamesShield = { enabled ->
                                lifecycleScope.launch { prefsManager.setAutoHideGames(enabled) }
                            },
                            onToggleDoubleTapLock = { enabled ->
                                lifecycleScope.launch { prefsManager.setDoubleTapLockEnabled(enabled) }
                            },
                            onToggleHideApp = { pkg ->
                                lifecycleScope.launch { prefsManager.toggleHideApp(pkg) }
                            },
                            onRequestSetDefault = { requestDefaultHomeLauncher() },
                            onBack = { isSettingsOpen = false }
                        )
                    } else {
                        HomeScreen(
                            context = this,
                            appList = appList,
                            autoHideSocial = autoHideSocial,
                            autoHideGames = autoHideGames,
                            doubleTapLockEnabled = doubleTapLockEnabled,
                            hiddenApps = hiddenApps,
                            onOpenSettings = { isSettingsOpen = true },
                            onRequestSetDefault = { requestDefaultHomeLauncher() }
                        )
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setupTotalImmersiveFullScreen()
        if (!hasFocus) {
            collapseSystemStatusBar(this)
        }
    }

    override fun onResume() {
        super.onResume()
        setupTotalImmersiveFullScreen()
        collapseSystemStatusBar(this)
    }

    private fun setupTotalImmersiveFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            collapseSystemStatusBar(this@MainActivity)
            view.onApplyWindowInsets(insets)
        }
    }

    fun requestDefaultHomeLauncher() {
        val isDefault = isDefaultLauncher(this)

        if (isDefault) {
            // USER WANTS TO RESET / UN-TRAP DEFAULT LAUNCHER!
            try {
                val fakeAlias = ComponentName(this, "$packageName.FakeHomeAlias")
                packageManager.setComponentEnabledSetting(
                    fakeAlias,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
                packageManager.setComponentEnabledSetting(
                    fakeAlias,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val resetIntents = listOf(
                Intent(Settings.ACTION_HOME_SETTINGS),
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
            )

            for (intent in resetIntents) {
                try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    return
                } catch (e: Exception) {
                    // Try next fallback intent
                }
            }

        } else {
            // USER WANTS TO SET AS DEFAULT LAUNCHER!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = getSystemService(RoleManager::class.java)
                if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                    try {
                        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                        defaultHomeLauncher.launch(intent)
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            val setIntents = listOf(
                Intent(Settings.ACTION_HOME_SETTINGS),
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            )

            for (intent in setIntents) {
                try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    return
                } catch (e: Exception) {
                    // Try next
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Intercept back button on Home Screen so pressing back stays on launcher!
    }

    companion object {
        fun collapseSystemStatusBar(context: Context) {
            try {
                @Suppress("DEPRECATION")
                val statusBarService = context.getSystemService("statusbar")
                val statusBarManagerExtra = Class.forName("android.app.StatusBarManager")
                val collapse = statusBarManagerExtra.getMethod("collapsePanels")
                collapse.invoke(statusBarService)
            } catch (e: Exception) {
                try {
                    @Suppress("DEPRECATION")
                    val statusBarService = context.getSystemService("statusbar")
                    val statusBarManagerExtra = Class.forName("android.app.StatusBarManager")
                    val collapse = statusBarManagerExtra.getMethod("collapse")
                    collapse.invoke(statusBarService)
                } catch (ex: Exception) {
                    // Ignored if unavailable
                }
            }
        }
    }
}
