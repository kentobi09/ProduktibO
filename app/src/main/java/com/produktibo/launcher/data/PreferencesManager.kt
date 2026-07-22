package com.produktibo.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "produktib_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val AUTO_HIDE_SOCIAL = booleanPreferencesKey("auto_hide_social")
        val AUTO_HIDE_GAMES = booleanPreferencesKey("auto_hide_games")
        val DOUBLE_TAP_LOCK_ENABLED = booleanPreferencesKey("double_tap_lock_enabled")
        val MINIMAL_LOCKSCREEN_ENABLED = booleanPreferencesKey("minimal_lockscreen_enabled")
        val HAS_PROMPTED_DEFAULT_LAUNCHER = booleanPreferencesKey("has_prompted_default_launcher")
        val TIME_FORMAT = stringPreferencesKey("time_format") // "24" or "12"
        val THEME_MODE = stringPreferencesKey("theme_mode") // "oled", "paper", "slate"
        val VISIBLE_APPS_CSV = stringPreferencesKey("visible_apps_csv")
        val HIDDEN_APPS_CSV = stringPreferencesKey("hidden_apps_csv")
    }

    val autoHideSocial: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[AUTO_HIDE_SOCIAL] ?: true
    }

    val autoHideGames: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[AUTO_HIDE_GAMES] ?: true
    }

    val doubleTapLockEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DOUBLE_TAP_LOCK_ENABLED] ?: true
    }

    val minimalLockscreenEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[MINIMAL_LOCKSCREEN_ENABLED] ?: false
    }

    val hasPromptedDefaultLauncher: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[HAS_PROMPTED_DEFAULT_LAUNCHER] ?: false
    }

    val timeFormat: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[TIME_FORMAT] ?: "24"
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "oled"
    }

    // Set of explicitly disabled/hidden app package names
    val hiddenAppsSet: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        val csv = prefs[HIDDEN_APPS_CSV] ?: ""
        if (csv.isEmpty()) emptySet() else csv.split(",").toSet()
    }

    suspend fun setAutoHideSocial(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTO_HIDE_SOCIAL] = enabled
        }
    }

    suspend fun setAutoHideGames(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTO_HIDE_GAMES] = enabled
        }
    }

    suspend fun setDoubleTapLockEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DOUBLE_TAP_LOCK_ENABLED] = enabled
        }
    }

    suspend fun setMinimalLockscreenEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MINIMAL_LOCKSCREEN_ENABLED] = enabled
        }
    }

    suspend fun setHasPromptedDefaultLauncher(prompted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[HAS_PROMPTED_DEFAULT_LAUNCHER] = prompted
        }
    }

    suspend fun setTimeFormat(format: String) {
        context.dataStore.edit { prefs ->
            prefs[TIME_FORMAT] = format
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    suspend fun toggleAppVisibility(packageName: String, isCurrentlyVisible: Boolean) {
        context.dataStore.edit { prefs ->
            val hiddenSet = (prefs[HIDDEN_APPS_CSV] ?: "").split(",").filter { it.isNotEmpty() }.toMutableSet()
            if (isCurrentlyVisible) {
                // User unchecks -> add to hidden set
                hiddenSet.add(packageName)
            } else {
                // User checks -> remove from hidden set so it becomes visible
                hiddenSet.remove(packageName)
            }
            prefs[HIDDEN_APPS_CSV] = hiddenSet.joinToString(",")
        }
    }
}
