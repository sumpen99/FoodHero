package com.example.foodhero.fragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.foodhero.R
import com.example.foodhero.activity.LoginActivity
import com.example.foodhero.databinding.FragmentLoginmainBinding
import com.example.foodhero.global.DialogInstance
import com.example.foodhero.global.FragmentInstance

class LoginMainFragment(intent: Intent) : BaseFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener(view)
        setBottomSheetDialog(R.layout.bottom_sheet_dialog,WRAP_CONTENT,DialogInstance.BOTTOM_SHEET_LOGIN)
        setBottomSheetEventButtons()
        setBottomSheetEventButtons()
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
        val userEnterButton = loginMainBinding().loginButtonLayout

        userEnterButton.setOnClickListener{
            bottomSheetDialog.show()
        }
    }

    private fun setBottomSheetEventButtons(){
        val userLogInBtn = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.userLoginLayout)
        val userSignUpBtn = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.userSignUpLayout)
        val userEnterAsGuestBtn = bottomSheetDialog.findViewById<LinearLayoutCompat>(R.id.enterAsGuestLayout)

        userEnterAsGuestBtn.setOnClickListener{
            bottomSheetDialog.dismiss()
            (parentActivity as LoginActivity).loginAsGuest()
        }

        userLogInBtn.setOnClickListener{
            bottomSheetDialog.dismiss()
            (parentActivity as LoginActivity).navigateToFragment(FragmentInstance.FRAGMENT_LOGIN_USER)

        }

        userSignUpBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            (parentActivity as LoginActivity).navigateToFragment(FragmentInstance.FRAGMENT_SIGN_UP)
        }
    }



    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */

    /*override fun onResume(){
        super.onResume()
    }

    override fun onPause(){
        super.onPause()
    }

    override fun onStop(){
        super.onStop()
    }*/

}