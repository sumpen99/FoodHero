package com.example.foodhero.fragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.foodhero.R
import com.example.foodhero.activity.LoginActivity
import com.example.foodhero.databinding.FragmentLoginuserBinding
import com.example.foodhero.databinding.FragmentSignupBinding
import com.example.foodhero.global.FragmentInstance
import com.example.foodhero.global.hideKeyboard


class SignUpFragment:BaseFragment() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener(view)
    }

    /*
    *   ##########################################################################
    *               IFRAGMENT FUNCTIONS
    *   ##########################################################################
    */

    override fun setFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
    }

    override fun setFragmentView() {
        baseView = binding.root
    }

    override fun getFragmentID(): FragmentInstance {
        return  FragmentInstance.FRAGMENT_SIGN_UP
    }

    override fun parentFragment(): FragmentInstance? {
        return FragmentInstance.FRAGMENT_LOGIN_HOME
    }

    /*
    *   ##########################################################################
    *               GET CORRECT BINDING
    *   ##########################################################################
    */

    private fun signUpUserBinding(): FragmentSignupBinding{
        return binding as FragmentSignupBinding
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setEventListener(view: View){
        emailField = signUpUserBinding().userEmail
        passwordField = signUpUserBinding().userPassword

        val logInBtn: Button = signUpUserBinding().logInBtn
        logInBtn.setOnClickListener{logIn()}

        view.setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_DOWN -> {emailField.hideKeyboard();passwordField.hideKeyboard()}
            }
            true
        }
    }

    private fun logIn(){
        if( illegalUserInput()){return}
        (parentActivity as LoginActivity).loginWithCredentials(emailField.text.toString(),passwordField.text.toString())
    }


    private fun illegalUserInput():Boolean{
        return (emailField.text.toString().isEmpty() || passwordField.text.toString().isEmpty())
    }


}