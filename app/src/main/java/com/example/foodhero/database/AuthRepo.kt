package com.example.foodhero.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface AuthCallback {
    fun isSuccessful(successful: Boolean, message : String)
}

class AuthRepo {
    private var auth : FirebaseAuth = Firebase.auth

    fun signIn(callback : AuthCallback, email : String, password : String) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.isSuccessful(false, "Du kan inte lämna fälten tomma.")
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                task -> if (task.isSuccessful) {
            callback.isSuccessful(true, "Konto skapat.")
        } else {
            callback.isSuccessful(false, task.exception.toString())
        }
        }
    }

    fun signUp(callback : AuthCallback, email : String, password : String) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.isSuccessful(false, "Du kan inte lämna fälten tomma.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task -> if (task.isSuccessful) {
            callback.isSuccessful(true, "Du har loggat in.")
        } else {
            callback.isSuccessful(false, task.exception.toString())
        }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn() : Boolean {
        return auth.currentUser != null
    }

    fun getEmail() : String {
        val email = auth.currentUser?.email ?: "Gäst"
        if (email.isEmpty()) {
            return "Gäst"
        }

        return email
    }
}