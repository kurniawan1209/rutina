package com.example.todoapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

class UserPreferencesRepository(private val context: Context) {

    private val KEY_THEME = stringPreferencesKey("app_theme")
    private val KEY_WIDGET_SHOW_COMPLETED = booleanPreferencesKey("widget_show_completed")

    val themeMode: Flow<AppThemeMode> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_THEME] ?: AppThemeMode.SYSTEM.name
        runCatching { AppThemeMode.valueOf(name) }.getOrDefault(AppThemeMode.SYSTEM)
    }

    val widgetShowCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_WIDGET_SHOW_COMPLETED] ?: false
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = mode.name
        }
    }

    suspend fun setWidgetShowCompleted(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_WIDGET_SHOW_COMPLETED] = show
        }
    }
}
