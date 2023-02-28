package com.example.foodhero.fragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.foodhero.R
import com.example.foodhero.global.DialogInstance
import com.example.foodhero.global.FragmentInstance
import com.example.foodhero.interfaces.IFragment
import com.example.foodhero.widgets.BottomSheetDialog
import com.example.foodhero.widgets.MessageToUser

abstract class BaseFragment: Fragment(), IFragment {
    protected lateinit var messageToUser: MessageToUser
    protected lateinit var bottomSheetDialog: BottomSheetDialog
    protected var baseView: View? = null
    protected var _binding: ViewBinding? = null
    protected val binding get() = _binding!!
    protected fun isBottomSheetInitialized() = ::bottomSheetDialog.isInitialized
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        setFragmentBinding(inflater,container)
        setFragmentView()
        setInfoToUser()
        initBottomSheetDialog()
        return getFragmentView()
    }

    private fun getFragmentView(): View {
        return baseView!!
    }

    private fun setInfoToUser(){
        messageToUser = MessageToUser(requireContext(),null)
    }

    private fun initBottomSheetDialog(){
        bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.MaterialDialogSheet, DialogInstance.BOTTOM_SHEET_INIT)
    }

    open fun setBottomSheetDialog(layoutID:Int,width:Int,dialogInstance:DialogInstance){
        bottomSheetDialog.dialogInstance = dialogInstance
        bottomSheetDialog.setContentView(layoutID)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.window?:return
        bottomSheetDialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,width)
        bottomSheetDialog.window!!.setGravity(Gravity.BOTTOM)
    }

    open fun dismissOpenBottomSheetDialog():Boolean{
        return (isBottomSheetInitialized() && bottomSheetDialog.isShowing)
    }

    open fun dismissNewBottomSheetDialogLayout(dialogInstance:DialogInstance):Boolean{
        return (isBottomSheetInitialized() && bottomSheetDialog.dialogInstance == dialogInstance)
    }

    /*
    *   ##########################################################################
    *               IFRAGMENT FUNCTIONS
    *   ##########################################################################
    */

    /*open fun updateMessageDialog(message:String,callback:(args:Any?)->Unit){
        messageToUser.setMessage(message)
        messageToUser.setTwoButtons()
        messageToUser.setPositiveCallback(callback)
    }*/

    open fun updateMessageDialog(message:String){
        messageToUser.setMessage(message)
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
    override fun onDestroy(){
        super.onDestroy()
        _binding = null
    }
}