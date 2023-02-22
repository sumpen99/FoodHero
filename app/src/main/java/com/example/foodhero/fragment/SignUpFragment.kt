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
import com.example.foodhero.struct.User


class SignUpFragment:BaseFragment() {
    private lateinit var emailField: EditText
    private lateinit var nameField: EditText
    private lateinit var phoneNumberField: EditText
    private lateinit var postalField: EditText
    private lateinit var cityField: EditText
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

    private fun getLoginActivity():LoginActivity{
        return requireActivity() as LoginActivity
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
        nameField = signUpUserBinding().userName
        phoneNumberField = signUpUserBinding().userPhonenumber
        postalField = signUpUserBinding().userPostalCode
        cityField = signUpUserBinding().userCity
        passwordField = signUpUserBinding().userPassword

        val signUpBtn: Button = signUpUserBinding().signUpBtn
        signUpBtn.setOnClickListener{signUp()}

        view.setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_DOWN -> {
                    emailField.hideKeyboard()
                    nameField.hideKeyboard()
                    phoneNumberField.hideKeyboard()
                    postalField.hideKeyboard()
                    cityField.hideKeyboard()
                    passwordField.hideKeyboard()}
            }
            true
        }
    }

    private fun signUp(){
        if(illegalUserInput()){return}
        getLoginActivity().signUpUser(createUser(),passwordField.text.toString())
    }

    private fun createUser():User{
        return User(
            emailField.text.toString(),
            nameField.text.toString(),
            phoneNumberField.text.toString(),
            postalField.text.toString(),
            cityField.text.toString()
        )
    }


    private fun illegalUserInput(): Boolean {
        return listOf(emailField, nameField, phoneNumberField, postalField, cityField, passwordField)
            .any { it.text.toString().isEmpty() }
    }
}