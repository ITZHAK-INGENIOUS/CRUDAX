package com.crudax.launcher.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("crudax_prefs")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val CLOCK_24H = booleanPreferencesKey("clock_24h")
        val SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val SHOW_DATE = booleanPreferencesKey("show_date")
    }

    val theme: Flow<String> = context.dataStore.data.map { it[Keys.THEME] ?: "system" }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }
    val clock24h: Flow<Boolean> = context.dataStore.data.map { it[Keys.CLOCK_24H] ?: true }
    val showSeconds: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_SECONDS] ?: false }
    val showDate: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_DATE] ?: true }

    suspend fun setTheme(value: String) {
        context.dataStore.edit { it[Keys.THEME] = value }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun setClock24h(value: Boolean) {
        context.dataStore.edit { it[Keys.CLOCK_24H] = value }
    }

    suspend fun setShowSeconds(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_SECONDS] = value }
    }

    suspend fun setShowDate(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_DATE] = value }
    }
}
