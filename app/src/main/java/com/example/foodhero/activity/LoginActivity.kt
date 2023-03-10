package com.example.foodhero.activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.database.FirestoreViewModel
import com.example.foodhero.databinding.ActivityLoginBinding
import com.example.foodhero.fragment.LoginMainFragment
import com.example.foodhero.fragment.LoginUserFragment
import com.example.foodhero.fragment.SignUpFragment
import com.example.foodhero.global.*
import com.example.foodhero.struct.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity: AppCompatActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var firestoreViewModel: FirestoreViewModel
    private var convertAnonumous = false
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    public override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setDataBinding()
        setViewModel()
        setOnBackNavigation()
        navigateOnEnter()

    }

    /*
    *   ##########################################################################
    *               SET BINDING
    *   ##########################################################################
    */

    private fun setDataBinding(){
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setViewModel(){
        firestoreViewModel = FirestoreViewModel()
    }

    /*
    *   ##########################################################################
    *               SET NAVIGATION IF USER DRAGS LEFT OR RIGHT SIDE OF SCREEN
    *   ##########################################################################
    */

    private fun setOnBackNavigation(){
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed(){
                navigateOnBackPressed()
            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }

    private fun navigateOnBackPressed(){
        if(!convertAnonumous)navigateToFragment(FragmentInstance.FRAGMENT_LOGIN_HOME)
        else finish()
    }

    /*
    *   ##########################################################################
    *               NAVIGATE BETWEEN FRAGMENTS
    *   ##########################################################################
    */

    private fun navigateOnEnter(){
        var moveToFragment = FragmentInstance.FRAGMENT_LOGIN_HOME
        val myIntent = intent
        if(myIntent.hasExtra("Fragment")){
            val fragmentString = myIntent.getStringExtra("Fragment")
            if(fragmentString!=null){
                convertAnonumous = true
                moveToFragment = FragmentInstance.valueOf(fragmentString)
            }
        }
        navigateToFragment(moveToFragment)
    }

   fun navigateToFragment(fragment: FragmentInstance){
        when(fragment){
            FragmentInstance.FRAGMENT_LOGIN_HOME->applyTransaction(LoginMainFragment())
            FragmentInstance.FRAGMENT_LOGIN_USER->applyTransaction(LoginUserFragment())
            FragmentInstance.FRAGMENT_SIGN_UP->applyTransaction(SignUpFragment())
            else -> {}
        }
    }

    private fun navigateOnLogin(){
        if(!convertAnonumous){
            moveToActivityAndFinish(Intent(this, MainActivity::class.java))
        }
        else{finish()}
    }

    private fun applyTransaction(frag: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.loginActivityLayout,frag).commit()
        }
    }

    /*
    *   ##########################################################################
    *               FIREBASE LOGIN SIGN UP
    *   ##########################################################################
    */
    fun loginAsGuest(){
        Firebase.auth.signInAnonymously().addOnCompleteListener(this){  task->
            if(task.isSuccessful){ navigateOnLogin()}
            else{showUserException(task)}
        }
    }

    fun loginWithCredentials(email: String,password: String){
        Firebase.auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){navigateOnLogin()}
                else{showUserException(task)}
            }
    }

    fun signUpUser(user: User, password: String){
        if(!convertAnonumous){
            Firebase.auth.createUserWithEmailAndPassword(user.email!!,password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        firestoreViewModel.saveUserToFirebase(user).addOnCompleteListener{t->
                            if(t.isSuccessful){navigateOnLogin()}
                            else{showMessage("UnExpected Error",Toast.LENGTH_SHORT)}
                        }
                    }
                    else{
                        showUserException(task)
                    }
                }
        }
        else{
            val credential = EmailAuthProvider.getCredential(user.email!!,password)
            Firebase.auth.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        firestoreViewModel.saveUserToFirebase(user).addOnCompleteListener{t->
                            if(t.isSuccessful){
                                showMessage("Konto Skapat",Toast.LENGTH_SHORT)
                                finish()
                            }
                            else{
                                showMessage("UnExpected Error",Toast.LENGTH_SHORT)
                            }
                        }
                    }
                    else{
                        showUserException(task)
                    }
                }
        }
    }

    private fun showUserException(task: Task<AuthResult>){
        val errorMessage = try{
            val errorCode = (task.exception as FirebaseAuthException).errorCode
            authErrors[errorCode] ?: R.string.error_login_default_error
        } catch(err:Exception){
            R.string.error_login_default_error
        }
        showMessage(getString(errorMessage), Toast.LENGTH_SHORT)
    }

    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */
    override fun onResume(){
        super.onResume()
    }

    override fun onPause(){
        super.onPause()
    }

    override fun onStop(){
        super.onStop()
    }
}