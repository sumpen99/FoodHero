package com.example.foodhero.database
import com.example.foodhero.global.USER_COLLECTION
import com.example.foodhero.struct.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

interface AuthCallback {
    fun isSuccessful(successful: Boolean, message : String)
}

class AuthRepo {
    private var auth : FirebaseAuth = Firebase.auth
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val firestoreStorage = Firebase.storage.reference



    fun signIn(callback : AuthCallback, email : String, password : String) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.isSuccessful(false, "Du kan inte lämna fälten tomma.")
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                task -> if (task.isSuccessful) {
            callback.isSuccessful(true, "Du har loggat in.")
        } else {
            callback.isSuccessful(false, task.exception.toString())
        }
        }
    }

    fun signUp(callback : AuthCallback, email : String, password : String, user: User) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.isSuccessful(false, "Du kan inte lämna fälten tomma.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task -> if(task.isSuccessful) {
                    firestoreDB.collection(USER_COLLECTION)
                    .document(user.email!!)
                    .set(user)
                    callback.isSuccessful(true, "Konto skapat.")
        } else {
            callback.isSuccessful(false, task.exception.toString())
        }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun userCustomClaims(): Task<GetTokenResult> {
        return auth.currentUser!!.getIdToken(false)
    }

    fun isUserLoggedIn() : Boolean {
        return auth.currentUser != null
    }

    fun userIsAnonymous():Boolean{
        return auth.currentUser?.isAnonymous?:true
    }

    fun userUid():String{
        return if(isUserLoggedIn()){
            auth.currentUser!!.uid
        }
        else{
            return ""
        }
    }

    fun getEmail() : String {
        val email = auth.currentUser?.email ?: "Gäst"
        if (email.isEmpty()) {
            return "Gäst"
        }

        return email
    }
}