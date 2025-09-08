package com.example.queuemanagmentsystem.SharedPrefernces

import android.content.Context

object AppPreferences {
    private const val PREF_NAME = "app_preferences"
    private const val KEY_LAST_SEEN_NOTIFICATION = "last_seen_notification"

    // Save last seen notification timestamp
    fun saveLastSeenNotificationTimestamp(context: Context, timestamp: Long) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_SEEN_NOTIFICATION, timestamp).apply()
    }

    // Get last seen notification timestamp
    fun getLastSeenNotificationTimestamp(context: Context): Long {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_SEEN_NOTIFICATION, 0L)
    }

    // add more preference keys here later

}
