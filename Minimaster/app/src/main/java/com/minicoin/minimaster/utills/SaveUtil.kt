package com.minicoin.minimaster.utills;

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log


object SaveUtil {

    private var mSharedPref: SharedPreferences? = null
    const val SHARED_DATA = "shared_data"

    fun init(context: Context) {
        if (mSharedPref == null) mSharedPref =
            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    fun read(key: String, defValue: String?): String? {
        val string = mSharedPref!!.getString(key, defValue)
        return string
    }

    fun write(key: String, value: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
        Log.d("testShared", "key($key), data: $value")
    }

    fun read(key: String, defValue: Boolean): Boolean {
        return mSharedPref!!.getBoolean(key, defValue)
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun read(key: String, defValue: Int): Int {
        return mSharedPref!!.getInt(key, defValue)
    }

    fun write(key: String, value: Int?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt(key, value!!).apply()
    }
}