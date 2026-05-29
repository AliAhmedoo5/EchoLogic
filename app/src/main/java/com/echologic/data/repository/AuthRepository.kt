package com.echologic.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.*
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/** Tri-state for auth so the UI never flashes the sign-in screen. */
sealed class AuthState {
    /** Firebase hasn't resolved the cached credential yet. */
    data object Loading : AuthState()
    /** No user is signed in. */
    data object Unauthenticated : AuthState()
    /** A user is signed in. */
    data class Authenticated(val user: FirebaseUser) : AuthState()
}

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleSignInClient: com.google.android.gms.auth.api.signin.GoogleSignInClient,
    private val settingsRepository: SettingsRepository,
    private val streakRepository: StreakRepository,
    private val database: com.echologic.data.local.AppDatabase,
    private val quoteRepository: QuoteRepository
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    /** Backward-compatible flow for code that just needs the FirebaseUser. */
    val currentUser: Flow<FirebaseUser?> = _authState.map { state ->
        when (state) {
            is AuthState.Authenticated -> state.user
            else -> null
        }
    }

    private var syncJob: Job? = null
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                // Cancel any pending cleanup or old syncs before starting new sync
                syncJob?.cancel()
                syncJob = syncScope.launch {
                    syncUserData(user.uid)
                }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                // Manual trigger after sign in (listener will also trigger, but this is safe)
                syncJob?.cancel()
                syncJob = syncScope.launch {
                    syncUserData(user.uid)
                }
                Result.success(user)
            } else {
                Result.failure(Exception("Google Sign-In failed: Null user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        // Cancel any active sync
        syncJob?.cancel()
        
        auth.signOut()
        googleSignInClient.signOut()
        settingsRepository.clearAll()
        
        // IMPORTANT: Do NOT clear all tables! Quotes are global data.
        // Only reset the user-specific favorite flags.
        withContext(Dispatchers.IO) {
            database.quoteDao().resetAllFavorites()
        }
        _authState.value = AuthState.Unauthenticated
        Log.d("AuthRepository", "Sign out complete: favorites reset, quotes preserved")
    }

    private suspend fun syncUserData(uid: String) {
        // 1. Reset all favorites (clean slate for this user)
        database.quoteDao().resetAllFavorites()
        
        // 2. Re-seed quotes if DB is empty (safety net)
        quoteRepository.ensureSeeded()
        
        // 3. Pull this user's favorites from cloud
        quoteRepository.mergeFavoritesWithCloud(uid)
        settingsRepository.syncSettingsWithCloud(uid)
        
        // 4. Restore streak data from cloud
        streakRepository.syncStreakFromCloud(uid)
        
        Log.d("AuthRepository", "syncUserData complete for user: $uid")
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            syncJob?.cancel()
            auth.currentUser?.delete()?.await()
            settingsRepository.clearAll()
            withContext(Dispatchers.IO) {
                database.quoteDao().resetAllFavorites()
            }
            _authState.value = AuthState.Unauthenticated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

