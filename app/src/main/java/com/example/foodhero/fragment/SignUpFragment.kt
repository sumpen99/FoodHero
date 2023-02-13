package com.example.foodhero.fragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.foodhero.activity.LoginActivity
import com.example.foodhero.databinding.FragmentSignupBinding
import com.example.foodhero.global.FragmentInstance
import com.example.foodhero.global.hideKeyboard


class SignUpFragment:BaseFragment() {
    private lateinit var emailField: EditText
    private lateinit var phoneNumberField: EditText
    private lateinit var adressField: EditText
    private lateinit var socialSecurityNumberField: EditText
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
        phoneNumberField = signUpUserBinding().userPhonenumber
        adressField = signUpUserBinding().userAdress
        passwordField = signUpUserBinding().userPassword

        val signUpBtn: Button = signUpUserBinding().signUpBtn
        signUpBtn.setOnClickListener{signUp()}

        view.setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_DOWN -> {
                    emailField.hideKeyboard();
                    phoneNumberField.hideKeyboard();
                    adressField.hideKeyboard();
                    passwordField.hideKeyboard()}
            }
            true
        }
    }

    private fun signUp(){
        if( illegalUserInput()){return}
        (parentActivity as LoginActivity)
            .loginWithCredentials(
                emailField.text.toString(),
                phoneNumberField.text.toString()
            )

    }


    private fun illegalUserInput():Boolean{
        return (
                emailField.text.toString().isEmpty() ||
                        phoneNumberField.text.toString().isEmpty() ||
                        adressField.text.toString().isEmpty() ||
                        passwordField.text.toString().isEmpty()

                )
    }

}