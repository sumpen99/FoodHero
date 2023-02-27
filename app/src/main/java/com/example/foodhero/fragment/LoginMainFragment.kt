package com.example.foodhero.fragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.foodhero.R
import com.example.foodhero.activity.AdminActivity
import com.example.foodhero.activity.LoginActivity
import com.example.foodhero.databinding.FragmentLoginmainBinding
import com.example.foodhero.global.DialogInstance
import com.example.foodhero.global.FragmentInstance
import com.example.foodhero.global.moveToActivityAndFinish
import javax.security.auth.login.LoginException

class LoginMainFragment : BaseFragment() {
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

//

    /*
    *   ##########################################################################
    *               GET CORRECT BINDING
    *   ##########################################################################
    */

    private fun loginMainBinding(): FragmentLoginmainBinding {
        return binding as FragmentLoginmainBinding
    }

    private fun getLoginActivity():LoginActivity{
        return requireActivity() as LoginActivity
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
        val adminLoginBtn = bottomSheetDialog.findViewById<ImageButton>(R.id.navigateAdmin)

        userEnterAsGuestBtn.setOnClickListener{
            bottomSheetDialog.dismiss()
            getLoginActivity().loginAsGuest()
        }

        userLogInBtn.setOnClickListener{
            bottomSheetDialog.dismiss()
            getLoginActivity().navigateToFragment(FragmentInstance.FRAGMENT_LOGIN_USER)

        }

        userSignUpBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            getLoginActivity().navigateToFragment(FragmentInstance.FRAGMENT_SIGN_UP)
        }

        adminLoginBtn.setOnClickListener{
            bottomSheetDialog.dismiss()
            getLoginActivity().moveToActivityAndFinish(Intent(getLoginActivity(),AdminActivity::class.java))

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