package com.example.foodhero.fragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodhero.activity.LoginActivity
import com.example.foodhero.databinding.FragmentLoginmainBinding
import com.example.foodhero.global.FragmentInstance
import com.example.foodhero.global.templateFunctionAny

class LoginMainFragment: BaseFragment() {

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
        _binding = FragmentLoginmainBinding.inflate(inflater, container, false)
    }

    override fun setFragmentView() {
        baseView = binding.root
    }

    override fun getFragmentID(): FragmentInstance {
        return  FragmentInstance.FRAGMENT_LOGIN_HOME
    }

    /*
    *   ##########################################################################
    *               GET CORRECT BINDING
    *   ##########################################################################
    */

    private fun loginMainBinding(): FragmentLoginmainBinding {
        return binding as FragmentLoginmainBinding
    }

    /*
    *   ##########################################################################
    *               SET EVENTLISTENER FOR BUTTON
    *   ##########################################################################
    */
    @SuppressLint("ClickableViewAccessibility")
    private fun setEventListener(view: View?){
        val userEnterButton = loginMainBinding().userEnterButton
        userEnterButton.setOnClickListener{
            updateMessageDialog("To Be Implemented",::templateFunctionAny)
            showMessage()
            //(parentActivity as LoginActivity).navigateToFragment(FragmentInstance.FRAGMENT_LOGIN_OPTIONS)
        }
    }




}