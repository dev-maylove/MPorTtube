package com.mporttube.data.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("mporttube_settings", Context.MODE_PRIVATE)

    private fun bool(key: String, default: Boolean) = MutableStateFlow(prefs.getBoolean(key, default))
    private fun text(key: String, default: String) = MutableStateFlow(prefs.getString(key, default) ?: default)

    private val autoplayState = bool("autoplay", true)
    private val backgroundState = bool("background_playback", true)
    private val wifiState = bool("wifi_only", false)
    private val notificationsState = bool("notifications", true)
    private val highQualityState = bool("high_quality", true)
    private val dynamicState = bool("dynamic_theme", false)
    private val privateState = bool("private_mode", false)
    private val qualityState = text("download_quality", "Best available")
    private val styleState = text("interface_style", "Premium dark")

    val autoplay: StateFlow<Boolean> = autoplayState
    val backgroundPlayback: StateFlow<Boolean> = backgroundState
    val wifiOnly: StateFlow<Boolean> = wifiState
    val notifications: StateFlow<Boolean> = notificationsState
    val highQuality: StateFlow<Boolean> = highQualityState
    val dynamicTheme: StateFlow<Boolean> = dynamicState
    val privateMode: StateFlow<Boolean> = privateState
    val downloadQuality: StateFlow<String> = qualityState
    val interfaceStyle: StateFlow<String> = styleState

    private fun set(key: String, value: Boolean, state: MutableStateFlow<Boolean>) {
        prefs.edit().putBoolean(key, value).apply(); state.value = value
    }
    private fun set(key: String, value: String, state: MutableStateFlow<String>) {
        prefs.edit().putString(key, value).apply(); state.value = value
    }
    fun setAutoplay(v: Boolean) = set("autoplay", v, autoplayState)
    fun setBackgroundPlayback(v: Boolean) = set("background_playback", v, backgroundState)
    fun setWifiOnly(v: Boolean) = set("wifi_only", v, wifiState)
    fun setNotifications(v: Boolean) = set("notifications", v, notificationsState)
    fun setHighQuality(v: Boolean) = set("high_quality", v, highQualityState)
    fun setDynamicTheme(v: Boolean) = set("dynamic_theme", v, dynamicState)
    fun setPrivateMode(v: Boolean) = set("private_mode", v, privateState)
    fun setDownloadQuality(v: String) = set("download_quality", v, qualityState)
    fun setInterfaceStyle(v: String) = set("interface_style", v, styleState)
}
