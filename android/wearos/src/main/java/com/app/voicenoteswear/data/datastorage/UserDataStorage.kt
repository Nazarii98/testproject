package com.app.voicenoteswear.data.datastorage

import javax.inject.Inject

class UserDataStorage @Inject constructor(
    private val sharedPrefs: SharedPrefsStorage
) {
    var accessToken: String
        get() = sharedPrefs.getString(Key.AccessToken)
        set(token) {
            sharedPrefs.putString(Key.AccessToken, token)
        }

    var refreshToken: String
        get() = sharedPrefs.getString(Key.RefreshToken)
        set(token) {
            sharedPrefs.putString(Key.RefreshToken, token)
        }
}