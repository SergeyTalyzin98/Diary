package com.sergeytalyzin.diary

import android.content.Context

class Password(private val context: Context) {

    private val appPreferences = "appPreferences"
    private val appPreferencesPass = "password"
    private val mPreferences = context.getSharedPreferences(appPreferences, Context.MODE_PRIVATE)

    fun savePassInSharedPreferences(password: String) {
        mPreferences.edit().putString(appPreferencesPass, password).apply()
    }

    fun deletePassFromSharedPreferences() {
        mPreferences.edit().remove(appPreferencesPass).apply()
    }

    fun getPassFromSharedPreferences() : String {
        if(mPreferences.contains(appPreferencesPass))
            return mPreferences.getString(appPreferencesPass, "")!!
        return ""
    }
}