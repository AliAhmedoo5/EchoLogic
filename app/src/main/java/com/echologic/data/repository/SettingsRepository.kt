package com.echologic.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val firestore: FirebaseFirestore
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("echologic_settings", Context.MODE_PRIVATE)

    private val defaultCategories = listOf(
        "Logic Loops", "Shower Thoughts", "Absurd", "Deep Dumb", 
        "Accidental Philosophy", "Brainrot", "Celebrity Dumb"
    )

    private val _availableCategories = MutableStateFlow(getCachedCategories())
    val availableCategories: StateFlow<List<String>> = _availableCategories

    private val _isPremium = MutableStateFlow(prefs.getBoolean("is_premium", false))
    val isPremium: StateFlow<Boolean> = _isPremium

    private val _isDailyNotificationEnabled = MutableStateFlow(prefs.getBoolean("daily_notification", false))
    val isDailyNotificationEnabled: StateFlow<Boolean> = _isDailyNotificationEnabled

    private val _notificationHour = MutableStateFlow(prefs.getInt("notification_hour", 8))  // Default 8 AM
    val notificationHour: StateFlow<Int> = _notificationHour

    private val _notificationMinute = MutableStateFlow(prefs.getInt("notification_minute", 0))  // Default :00
    val notificationMinute: StateFlow<Int> = _notificationMinute

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _accentColor = MutableStateFlow(prefs.getInt("accent_color", 0xFFF4FF4D.toInt())) // Default Electric Yellow
    val accentColor: StateFlow<Int> = _accentColor

    private val _selectedCategories = MutableStateFlow(getSelectedCategoriesFromPrefs())
    val selectedCategories: StateFlow<Set<String>> = _selectedCategories

    fun setPremium(isPremium: Boolean) {
        prefs.edit().putBoolean("is_premium", isPremium).apply()
        _isPremium.value = isPremium
    }

    /** Returns true if user is currently premium (from local cache). */
    fun isCurrentlyPremium(): Boolean = prefs.getBoolean("is_premium", false)

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
        _isDarkMode.value = enabled
    }

    fun setAccentColor(color: Int) {
        prefs.edit().putInt("accent_color", color).apply()
        _accentColor.value = color
    }

    fun setDailyNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("daily_notification", enabled).apply()
        _isDailyNotificationEnabled.value = enabled
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt("notification_hour", hour)
            .putInt("notification_minute", minute)
            .apply()
        _notificationHour.value = hour
        _notificationMinute.value = minute
    }

    fun setSelectedCategories(categories: Set<String>) {
        // IMPORTANT: Always creates a new set to avoid SharedPreferences internal set mutation
        val categoriesCopy = categories.toSet() 
        prefs.edit().putStringSet("selected_categories", categoriesCopy).apply()
        _selectedCategories.value = categoriesCopy
    }

    suspend fun syncAvailableCategories() {
        try {
            val snapshot = firestore.collection("metadata").document("app_config").get().await()
            val categories = snapshot.get("categories") as? List<String>
            if (categories != null && categories.isNotEmpty()) {
                val categorySet = categories.toSet()
                prefs.edit().putStringSet("available_categories_cache", categorySet).apply()
                _availableCategories.value = categories
                
                // If no categories are selected, update selection to include all new categories
                if (prefs.getStringSet("selected_categories", null) == null) {
                    _selectedCategories.value = categorySet
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("SettingsRepository", "Category sync failed: ${e.message}")
        }
    }

    private fun getCachedCategories(): List<String> {
        val cached = prefs.getStringSet("available_categories_cache", null)
        return cached?.toList() ?: defaultCategories
    }

    private fun getSelectedCategoriesFromPrefs(): Set<String> {
        val saved = prefs.getStringSet("selected_categories", null)
        // Return a copy of the set or the default available categories
        return saved?.toSet() ?: getCachedCategories().toSet()
    }

    fun clearAll() {
        // Preserve notification preferences across sign-out (they're device-specific)
        val notifEnabled = prefs.getBoolean("daily_notification", false)
        val notifHour = prefs.getInt("notification_hour", 8)
        val notifMinute = prefs.getInt("notification_minute", 0)
        
        prefs.edit().clear().apply()
        
        // Restore notification prefs
        prefs.edit()
            .putBoolean("daily_notification", notifEnabled)
            .putInt("notification_hour", notifHour)
            .putInt("notification_minute", notifMinute)
            .apply()
        
        _isPremium.value = false
        _isDarkMode.value = true
        _accentColor.value = 0xFFF4FF4D.toInt()
        _selectedCategories.value = defaultCategories.toSet()
        _availableCategories.value = defaultCategories
        // Don't reset notification flows — they were preserved above
    }

    suspend fun syncSettingsWithCloud(userId: String) {
        try {
            val doc = firestore.collection("users").document(userId)
                .collection("settings").document("preferences").get().await()
            
            if (doc.exists()) {
                // STICKY PREMIUM: Cloud can upgrade (false→true) but NEVER downgrade (true→false).
                val cloudPremium = doc.getBoolean("isPremium") ?: false
                val localPremium = isCurrentlyPremium()
                if (cloudPremium || !localPremium) {
                    setPremium(cloudPremium)
                }
                if (localPremium && !cloudPremium) {
                    uploadSettingsToCloud(userId)
                }
                
                val darkMode = doc.getBoolean("isDarkMode") ?: true
                val accent = doc.getLong("accentColor")?.toInt() ?: 0xFFF4FF4D.toInt()
                val categories = doc.get("selectedCategories") as? List<String>
                
                // Sync notification preferences from cloud
                val cloudNotifEnabled = doc.getBoolean("isDailyNotificationEnabled")
                val cloudNotifHour = doc.getLong("notificationHour")?.toInt()
                val cloudNotifMinute = doc.getLong("notificationMinute")?.toInt()
                
                setDarkMode(darkMode)
                setAccentColor(accent)
                if (categories != null) {
                    setSelectedCategories(categories.toSet())
                }
                // Apply cloud notification prefs if present
                if (cloudNotifEnabled != null) {
                    setDailyNotificationEnabled(cloudNotifEnabled)
                }
                if (cloudNotifHour != null && cloudNotifMinute != null) {
                    setNotificationTime(cloudNotifHour, cloudNotifMinute)
                }
            } else {
                // No cloud doc exists yet — push local settings to cloud
                uploadSettingsToCloud(userId)
            }
        } catch (e: Exception) {
            android.util.Log.w("SettingsRepository", "Cloud settings sync failed: ${e.message}")
        }
    }

    suspend fun uploadSettingsToCloud(userId: String) {
        try {
            val data = mapOf(
                "isPremium" to _isPremium.value,
                "isDarkMode" to _isDarkMode.value,
                "accentColor" to _accentColor.value.toLong(),
                "selectedCategories" to _selectedCategories.value.toList(),
                "isDailyNotificationEnabled" to _isDailyNotificationEnabled.value,
                "notificationHour" to _notificationHour.value.toLong(),
                "notificationMinute" to _notificationMinute.value.toLong()
            )
            firestore.collection("users").document(userId)
                .collection("settings").document("preferences").set(data).await()
        } catch (e: Exception) {
            android.util.Log.w("SettingsRepository", "Cloud settings upload failed: ${e.message}")
        }
    }
}
