package com.example.foodhero.fragment
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.foodhero.R
import com.example.foodhero.global.FragmentInstance
import com.example.foodhero.interfaces.IFragment
import com.example.foodhero.widgets.MessageToUser

abstract class BaseFragment: Fragment(), IFragment {
    protected lateinit var activityContext: Context
    protected lateinit var parentActivity: Activity
    protected lateinit var messageToUser: MessageToUser
    protected lateinit var bottomSheetDialog: Dialog
    protected var baseView: View? = null
    protected var _binding: ViewBinding? = null
    protected val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        if(baseView!=null){return baseView!!}
        setFragmentBinding(inflater,container)
        setFragmentView()
        setParentActivity()
        setActivityContext()
        setInfoToUser()
        return getFragmentView()
    }

    private fun setParentActivity(){
        parentActivity = requireActivity()
    }

    private fun setActivityContext(){
        activityContext = requireContext()
    }

    private fun getFragmentView(): View {
        return baseView!!
    }

    private fun setInfoToUser(){
        messageToUser = MessageToUser(parentActivity,null)
    }

    open fun setBottomSheetDialog(layoutID:Int){
        bottomSheetDialog = Dialog(parentActivity,R.style.MaterialDialogSheet)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.window?:return
        bottomSheetDialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog.window!!.setGravity(Gravity.BOTTOM)
    }



    /*
    *   ##########################################################################
    *               IFRAGMENT FUNCTIONS
    *   ##########################################################################
    */

    open fun updateMessageDialog(message:String,callback:(args:Any?)->Unit){
        messageToUser = MessageToUser(parentActivity,null)
        messageToUser.setMessage(message)
        messageToUser.setTwoButtons()
        messageToUser.setPositiveCallback(callback)
    }

    open fun showMessage(){
        messageToUser.showMessage()
    }

    override fun parentFragment(): FragmentInstance? {
        return null
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