package com.echologic.data.repository

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUser = auth.currentUser

    fun signOut() {
        auth.signOut()
    }
}
