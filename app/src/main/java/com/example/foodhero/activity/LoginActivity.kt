package com.example.foodhero.activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.databinding.ActivityLoginBinding
import com.example.foodhero.fragment.LoginMainFragment
import com.example.foodhero.fragment.LoginUserFragment
import com.example.foodhero.fragment.SignUpFragment
import com.example.foodhero.global.*
import com.example.foodhero.interfaces.IFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity: AppCompatActivity() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    public override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setDataBinding()
        setOnBackNavigation()
        navigateToFragment(FragmentInstance.FRAGMENT_LOGIN_HOME)
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
        val moveToParent = getFragmentParent()
        moveToParent?:return
        navigateToFragment(moveToParent)
    }

    private fun getFragmentParent():FragmentInstance?{
        return try{
            val size = supportFragmentManager.fragments.size
            (supportFragmentManager.fragments[size-1] as IFragment).parentFragment()
        }catch(err:Exception){
            null
        }
    }

    /*
    *   ##########################################################################
    *               NAVIGATE BETWEEN FRAGMENTS
    *   ##########################################################################
    */

   fun navigateToFragment(fragment: FragmentInstance){
        when(fragment){
            FragmentInstance.FRAGMENT_LOGIN_HOME->applyTransaction(LoginMainFragment())
            FragmentInstance.FRAGMENT_LOGIN_USER->applyTransaction(LoginUserFragment())
            FragmentInstance.FRAGMENT_SIGN_UP->applyTransaction(SignUpFragment())
            FragmentInstance.FRAGMENT_LOGIN_OPTIONS->{}
            else -> {}
        }
    }

    private fun navigateOnLogin(){
        moveToActivity(Intent(this, MainActivity::class.java))
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
    //firebase.auth().signInAnonymously()
    //firebase.auth().currentUser.isAnonymous
    //if (firebase.auth().currentUser.isAnonymous) {
    //  var cred = firebase.auth.EmailAuthProvider.credential(email, password);
    //  firebase.auth().currentUser.linkAndRetrieveDataWithCredential(cred);
    //}
    fun loginAsGuest(){
        Firebase.auth.signInAnonymously().addOnCompleteListener(this){  task->
            if(task.isSuccessful){
                navigateOnLogin()
            }
            else{
                showUserException(task)
            }

        }
    }

    fun loginWithCredentials(
        email: String,
        password: String
    ){
        Firebase.auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){navigateOnLogin()}
                else{showUserException(task)}
            }
    }

    fun logIn(){

    }

    fun signUp(){

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
        logMessage("on resume login")

    }

    override fun onPause(){
        super.onPause()
        logMessage("on pause login")

    }

    override fun onStop(){
        super.onStop()
        logMessage("on stop login")

    }
}