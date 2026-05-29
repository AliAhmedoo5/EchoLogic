package com.echologic.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("echologic_streaks", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _currentStreak = MutableStateFlow(prefs.getInt("current_streak", 0))
    val currentStreak: StateFlow<Int> = _currentStreak

    private val _longestStreak = MutableStateFlow(prefs.getInt("longest_streak", 0))
    val longestStreak: StateFlow<Int> = _longestStreak

    init {
        checkAndLogActivity()
    }

    fun checkAndLogActivity() {
        val todayStr = dateFormat.format(Date())
        val lastActiveDateStr = prefs.getString("last_active_date", null)

        var streak = prefs.getInt("current_streak", 0)
        var longest = prefs.getInt("longest_streak", 0)

        if (lastActiveDateStr == null) {
            // First time opening app
            streak = 1
        } else if (lastActiveDateStr != todayStr) {
            val today = dateFormat.parse(todayStr)
            val lastActive = dateFormat.parse(lastActiveDateStr)

            if (today != null && lastActive != null) {
                val diffInMillis = today.time - lastActive.time
                val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                if (diffInDays == 1L) {
                    // Consecutive day
                    streak += 1
                } else if (diffInDays > 1L) {
                    // Streak broken
                    streak = 1
                }
            }
        }
        // else: same day, streak unchanged

        // Track longest streak
        if (streak > longest) {
            longest = streak
        }

        // Update preferences
        prefs.edit()
            .putString("last_active_date", todayStr)
            .putInt("current_streak", streak)
            .putInt("longest_streak", longest)
            .apply()

        _currentStreak.value = streak
        _longestStreak.value = longest

        // Sync to cloud in background
        syncStreakToCloud()
    }

    private fun syncStreakToCloud() {
        val uid = auth.currentUser?.uid ?: return
        scope.launch {
            try {
                val data = mapOf(
                    "currentStreak" to _currentStreak.value,
                    "longestStreak" to _longestStreak.value,
                    "lastActiveDate" to prefs.getString("last_active_date", ""),
                    "updatedAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(uid)
                    .collection("stats").document("streak")
                    .set(data).await()
                Log.d("StreakRepository", "Streak synced to cloud: ${_currentStreak.value}")
            } catch (e: Exception) {
                Log.w("StreakRepository", "Failed to sync streak: ${e.message}")
            }
        }
    }

    /** Call on sign-in to restore streak from cloud if higher than local. */
    suspend fun syncStreakFromCloud(userId: String) {
        try {
            val doc = firestore.collection("users").document(userId)
                .collection("stats").document("streak").get().await()
            
            if (doc.exists()) {
                val cloudStreak = doc.getLong("currentStreak")?.toInt() ?: 0
                val cloudLongest = doc.getLong("longestStreak")?.toInt() ?: 0
                val cloudLastDate = doc.getString("lastActiveDate") ?: ""
                
                val localStreak = _currentStreak.value
                val localLongest = _longestStreak.value
                
                // Use the higher streak (prefer cloud if user reinstalled)
                if (cloudStreak > localStreak) {
                    _currentStreak.value = cloudStreak
                    prefs.edit().putInt("current_streak", cloudStreak).apply()
                    
                    // Also restore the last active date so consecutive-day logic works
                    if (cloudLastDate.isNotEmpty()) {
                        prefs.edit().putString("last_active_date", cloudLastDate).apply()
                    }
                }
                
                // Always take the higher longest streak
                val mergedLongest = maxOf(cloudLongest, localLongest)
                _longestStreak.value = mergedLongest
                prefs.edit().putInt("longest_streak", mergedLongest).apply()
                
                // Re-check today's activity with the restored data
                checkAndLogActivity()
                
                Log.d("StreakRepository", "Streak restored from cloud: current=$cloudStreak, longest=$cloudLongest")
            }
        } catch (e: Exception) {
            Log.w("StreakRepository", "Failed to restore streak from cloud: ${e.message}")
        }
    }
}
