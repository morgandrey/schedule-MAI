package com.example.schedulemai.utils

import android.content.Context
import android.content.SharedPreferences


/**
 * Created by Andrey Morgunov on 07/03/2021.
 */


interface SharedPreferencesService {
    fun setGroup(value: String)
    fun getGroup(): String?
    fun deleteGroup()
}

class SharedPreferencesServiceImpl(context: Context): SharedPreferencesService {
    private val MY_APP_PREFERENCES = "GROUP"
    private val PREF_USER_LEARNED_DRAWER = "Group"

    private var sharedPrefs: SharedPreferences? = null

    init {
        sharedPrefs = context.applicationContext
            .getSharedPreferences(MY_APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun getGroup(): String? {
        return sharedPrefs!!.getString(PREF_USER_LEARNED_DRAWER, null)
    }

    override fun deleteGroup() {
        sharedPrefs!!.edit().remove(PREF_USER_LEARNED_DRAWER).apply()
    }

    override fun setGroup(value: String) {
        sharedPrefs!!.edit().also {
            it.putString(PREF_USER_LEARNED_DRAWER, value)
            it.apply()
        }
    }
}