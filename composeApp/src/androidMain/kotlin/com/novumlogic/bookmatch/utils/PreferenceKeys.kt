package com.novumlogic.bookmatch.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys{
    val LAST_RECOMMENDATION_TIME = longPreferencesKey("last_recommendation_time")
    val CATEGORY_LIST = stringPreferencesKey("category_list")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val USER_LOGGED = booleanPreferencesKey("user_logged")
    const val PREF_NAME = "book_match_prefs"
    val CATEGORY_SHOWN = booleanPreferencesKey("category_shown")
}