package com.arthuralexandryan.footballquiz.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object Prefer {

    @JvmStatic fun getStringPreference(context: Context, pref: String, def: String?): String? =
        PreferenceManager.getDefaultSharedPreferences(context).getString(pref, def)

    @JvmStatic fun getIntPreference(context: Context, pref: String, def: Int): Int =
        PreferenceManager.getDefaultSharedPreferences(context).getInt(pref, def)

    @JvmStatic fun getLongPreference(context: Context, pref: String, def: Long): Long =
        PreferenceManager.getDefaultSharedPreferences(context).getLong(pref, def)

    @JvmStatic fun setIntPreference(c: Context, pref: String, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putInt(pref, value).apply()
    }

    @JvmStatic fun setStringPreference(c: Context, pref: String, value: String) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putString(pref, value).apply()
    }

    @JvmStatic fun setLongPreference(c: Context, pref: String, value: Long) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putLong(pref, value).apply()
    }

    @JvmStatic fun getBooleanPreference(context: Context, pref: String, def: Boolean): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(pref, def)

    @JvmStatic fun setBooleanPreference(c: Context, pref: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(pref, value).apply()
    }

    @JvmStatic fun setFloatPreference(c: Context, pref: String, value: Float) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putFloat(pref, value).apply()
    }

    @JvmStatic fun getFloatPreference(c: Context, pref: String, defaultVal: Float): Float =
        PreferenceManager.getDefaultSharedPreferences(c).getFloat(pref, defaultVal)

    @JvmStatic fun getSharedPreference(c: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(c)

    @JvmStatic fun getSharedPreferenceEditor(c: Context): SharedPreferences.Editor =
        PreferenceManager.getDefaultSharedPreferences(c).edit()
}
