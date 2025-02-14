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
    private const val RULES_BUTTON_ENABLED_KEY = "RulesButtonEnabled"
    private const val SELECTED_PROFILE_KEY = "SelectedProfile"

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

    private fun saveDeviceInfo(context: Context, deviceInfo: DeviceInfo) {
        val deviceList = getDeviceInfoList(context).toMutableList()
        val existingDeviceIndex = deviceList.indexOfFirst { it.deviceId == deviceInfo.deviceId }

        if (existingDeviceIndex != -1) {
            deviceList[existingDeviceIndex] = deviceInfo
        } else {
            deviceList.add(deviceInfo)
        }

        saveDeviceInfoList(context, deviceList)
    }

    private fun getDeviceInfo(context: Context, deviceId: String): DeviceInfo? {
        val deviceList = getDeviceInfoList(context)
        return deviceList.find { it.deviceId == deviceId }
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
        editor.remove(DEVICE_INFO_KEY)
        editor.apply()
    }


    fun isRulesButtonEnabled(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(RULES_BUTTON_ENABLED_KEY, true) // Default is enabled
    }

    fun setRulesButtonEnabled(context: Context, isEnabled: Boolean) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(RULES_BUTTON_ENABLED_KEY, isEnabled).apply()
    }

    fun saveSelectedProfile(context: Context, profileTitle: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(SELECTED_PROFILE_KEY, profileTitle).apply()
    }


    fun getSelectedProfile(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SELECTED_PROFILE_KEY, null)
    }

    fun saveProfileForDevice(context: Context, deviceId: String, profile: String) {
        val deviceInfo = getDeviceInfo(context, deviceId)
        deviceInfo?.profile = profile
        if (deviceInfo != null) {
            saveDeviceInfo(context, deviceInfo)
        }
    }
}
