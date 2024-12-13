package com.app.lockcompose

import DeviceInfo
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesHelper {

    private const val PREFS_NAME = "MyAppPrefs"
    private const val DEVICE_INFO_KEY = "DeviceInfoList"
    private const val SELECTED_DEVICE_KEY = "SelectedDevice"

    fun saveDeviceInfoList(context: Context, deviceInfoList: List<DeviceInfo>) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(deviceInfoList)
        editor.putString(DEVICE_INFO_KEY, json)
        editor.apply()
    }

    fun getDeviceInfoList(context: Context): List<DeviceInfo> {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(DEVICE_INFO_KEY, null)
        return Gson().fromJson(json, object : TypeToken<List<DeviceInfo>>() {}.type) ?: emptyList()
    }

    fun saveSelectedDevice(context: Context, deviceInfo: DeviceInfo) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(deviceInfo)
        editor.putString(SELECTED_DEVICE_KEY, json)
        editor.apply()
    }

    fun getSelectedDevice(context: Context): DeviceInfo? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(SELECTED_DEVICE_KEY, null)
        return json?.let { Gson().fromJson(it, DeviceInfo::class.java) }
    }

    fun deleteDeviceInfoList(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(DEVICE_INFO_KEY) // Remove the DeviceInfoList key
        editor.apply()
    }
}
