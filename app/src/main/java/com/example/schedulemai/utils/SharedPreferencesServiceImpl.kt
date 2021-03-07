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
    private val APP_PREFERENCES = "GROUP"
    private val GROUP_PREF = "Group"

    private var sharedPrefs: SharedPreferences? = null

    init {
        sharedPrefs = context.applicationContext
            .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun getGroup(): String? {
        return sharedPrefs!!.getString(GROUP_PREF, null)
    }

    override fun deleteGroup() {
        sharedPrefs!!.edit().remove(GROUP_PREF).apply()
    }

    override fun setGroup(value: String) {
        sharedPrefs!!.edit().also {
            it.putString(GROUP_PREF, value)
            it.apply()
        }
    }
}