package com.app.voicenoteswear.data.datastorage

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPrefsStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getString(key: String, defValue: String = ""): String {
        return sharedPreferences.getString(key, defValue) ?: ""
    }

    fun putString(key: String, value: String) {
        editor.putString(key, value).commit()
    }

    fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).commit()
    }
}