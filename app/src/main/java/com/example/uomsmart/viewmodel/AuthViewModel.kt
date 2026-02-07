package com.example.uomsmart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uomsmart.data.repository.AuthRepository
import com.example.uomsmart.data.repository.UserProfile
import com.example.uomsmart.data.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var currentUser by mutableStateOf<FirebaseUser?>(authRepository.currentUser)
        private set

    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    val isLoggedIn: Boolean
        get() = currentUser != null

    init {
        if (currentUser != null) {
            fetchUserProfile()
        }
    }

    fun fetchUserProfile() {
        currentUser?.let { user ->
            viewModelScope.launch {
                userRepository
                        .getUserProfile(user.uid)
                        .onSuccess { profile -> userProfile = profile }
                        .onFailure {
                            // Handle failure silently or show error
                        }
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all fields"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            authRepository
                    .signIn(email, password)
                    .onSuccess { user ->
                        currentUser = user
                        fetchUserProfile()
                        onSuccess()
                    }
                    .onFailure { e -> errorMessage = e.message ?: "Sign in failed" }

            isLoading = false
        }
    }

    fun signUp(name: String, email: String, password: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all fields"
            return
        }

        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            authRepository
                    .signUp(email, password)
                    .onSuccess { user ->
                        // Create user profile in Firestore
                        userRepository.createUserProfile(user.uid, email, name)
                        currentUser = user
                        fetchUserProfile()
                        onSuccess()
                    }
                    .onFailure { e -> errorMessage = e.message ?: "Sign up failed" }

            isLoading = false
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            authRepository
                    .signInWithGoogle(idToken)
                    .onSuccess { user ->
                        // Check if user profile exists, if not create it
                        userRepository.getUserProfile(user.uid).onFailure {
                            userRepository.createUserProfile(
                                    userId = user.uid,
                                    email = user.email ?: "",
                                    name = user.displayName ?: "User"
                            )
                        }
                        currentUser = user
                        fetchUserProfile()
                        onSuccess()
                    }
                    .onFailure { e -> errorMessage = e.message ?: "Google sign in failed" }

            isLoading = false
        }
    }

    fun signOut() {
        authRepository.signOut()
        currentUser = null
        userProfile = null
    }

    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit) {
        if (email.isBlank()) {
            errorMessage = "Please enter your email"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            authRepository.sendPasswordResetEmail(email).onSuccess { onSuccess() }.onFailure { e ->
                errorMessage = e.message ?: "Failed to send reset email"
            }

            isLoading = false
        }
    }

    fun clearError() {
        errorMessage = null
    }
}
