package com.org.wiichat.core

import android.content.Context
import android.content.SharedPreferences

class PersistentStorage(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("pref_user", Context.MODE_PRIVATE)

    fun createUserId() {
        val rnd = (0..9999999).random()
        sharedPreferences.edit().apply {
            putLong("rnd_user_key", rnd.toLong())
        }.apply()
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong("rnd_user_key", 0)
    }
}