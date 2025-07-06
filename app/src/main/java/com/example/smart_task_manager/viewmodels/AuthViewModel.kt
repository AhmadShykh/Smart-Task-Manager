package com.example.smart_task_manager.viewmodels

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth


class AuthViewModel : androidx.lifecycle.ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    fun loginUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Email and password cannot be empty")
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(true, "")
            else onResult(false, task.exception?.message ?: "Login failed")
        }
    }

    fun signupUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Email and password cannot be empty")
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(true, "")
            else onResult(false, task.exception?.message ?: "Signup failed")
        }
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean, String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(true, "")
            else onResult(false, task.exception?.message ?: "Google Sign-In failed")
        }
    }
}