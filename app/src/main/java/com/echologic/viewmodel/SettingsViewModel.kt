package com.echologic.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.echologic.data.repository.QuoteRepository
import com.echologic.data.repository.SettingsRepository
import com.echologic.worker.QuoteWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: QuoteRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: com.echologic.data.repository.AuthRepository,
    private val billingRepository: com.echologic.data.repository.BillingRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun deleteAccount(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.deleteAccount()
            onComplete(result.isSuccess)
        }
    }

    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = authRepository.currentUser
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), authRepository.currentUser.run { null }) // Safe way to get initial value

    val isPremium: StateFlow<Boolean> = settingsRepository.isPremium
    val isDailyNotificationEnabled: StateFlow<Boolean> = settingsRepository.isDailyNotificationEnabled
    val isDarkMode: StateFlow<Boolean> = settingsRepository.isDarkMode
    val accentColor: StateFlow<Int> = settingsRepository.accentColor
    val selectedCategories: StateFlow<Set<String>> = settingsRepository.selectedCategories
    val notificationHour: StateFlow<Int> = settingsRepository.notificationHour
    val notificationMinute: StateFlow<Int> = settingsRepository.notificationMinute

    init {
        // Re-schedule daily notification on ViewModel init if the pref is enabled.
        // This ensures the WorkManager job survives app restarts even if the
        // user never visits Settings again.
        if (settingsRepository.isDailyNotificationEnabled.value) {
            scheduleDailyNotification(
                settingsRepository.notificationHour.value,
                settingsRepository.notificationMinute.value
            )
        }
    }

    fun setDarkMode(enabled: Boolean) {
        settingsRepository.setDarkMode(enabled)
    }

    fun setAccentColor(color: Int) {
        settingsRepository.setAccentColor(color)
    }

    fun toggleDailyNotification(enabled: Boolean) {
        if (!isPremium.value) return // Gate behind premium

        settingsRepository.setDailyNotificationEnabled(enabled)

        if (enabled) {
            scheduleDailyNotification(
                settingsRepository.notificationHour.value,
                settingsRepository.notificationMinute.value
            )
        } else {
            WorkManager.getInstance(context).cancelUniqueWork("daily_quote_work")
        }

        // Sync to cloud
        uploadSettingsToCloudSilently()
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        settingsRepository.setNotificationTime(hour, minute)

        // Reschedule if notifications are enabled
        if (settingsRepository.isDailyNotificationEnabled.value) {
            scheduleDailyNotification(hour, minute)
        }

        // Sync to cloud
        uploadSettingsToCloudSilently()
    }

    /**
     * Schedules a daily notification at the user's chosen time.
     * Calculates the initial delay from now until the next occurrence of [hour]:[minute]
     * in the user's local timezone, then repeats every 24 hours.
     */
    private fun scheduleDailyNotification(hour: Int, minute: Int) {
        val workManager = WorkManager.getInstance(context)

        // Calculate delay until the next occurrence of the target time
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the target time has already passed today, schedule for tomorrow
        if (target.before(now) || target == now) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelayMillis = target.timeInMillis - now.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<QuoteWorker>(24, TimeUnit.HOURS, 1, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_quote_work",
            ExistingPeriodicWorkPolicy.UPDATE,  // UPDATE replaces existing with new schedule
            dailyWorkRequest
        )
    }

    /** Silently uploads settings to cloud without blocking the UI. */
    private fun uploadSettingsToCloudSilently() {
        viewModelScope.launch {
            authRepository.currentUser.firstOrNull()?.uid?.let { uid ->
                settingsRepository.uploadSettingsToCloud(uid)
            }
        }
    }

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError: StateFlow<String?> = _uploadError

    val availableCategories: StateFlow<List<String>> = settingsRepository.availableCategories
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    fun upgradeToPremium(activity: android.app.Activity) {
        billingRepository.launchBillingFlow(activity)
    }

    fun forceUnlockPremium() {
        settingsRepository.setPremium(true)
        viewModelScope.launch {
            authRepository.currentUser.firstOrNull()?.uid?.let { uid ->
                settingsRepository.uploadSettingsToCloud(uid)
            }
        }
    }

    fun toggleCategory(category: String) {
        val current = selectedCategories.value.toMutableSet()
        if (current.contains(category)) {
            // Prevent unselecting all (must have at least one)
            if (current.size > 1) {
                current.remove(category)
            }
        } else {
            current.add(category)
        }
        settingsRepository.setSelectedCategories(current)
    }

    fun uploadDataToCloud(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isUploading.value = true
            _uploadError.value = null
            try {
                repository.uploadSeededQuotesToFirestore()
                onSuccess()
            } catch (e: Exception) {
                _uploadError.value = e.message ?: "Unknown error"
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun clearError() {
        _uploadError.value = null
    }

    fun submitQuote(text: String, author: String, category: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUser.firstOrNull()?.uid
                repository.submitQuote(text, author, category, userId)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
