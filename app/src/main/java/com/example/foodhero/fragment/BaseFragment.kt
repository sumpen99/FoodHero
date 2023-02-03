package com.example.foodhero.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.foodhero.interfaces.IFragment
import com.example.foodhero.widgets.MessageToUser

abstract class BaseFragment: Fragment(), IFragment {
    protected lateinit var activityContext: Context
    protected lateinit var parentActivity: Activity
    protected lateinit var messageToUser: MessageToUser
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


    /*
    *   ##########################################################################
    *               IFRAGMENT FUNCTIONS
    *   ##########################################################################
    */

    private fun setInfoToUser(){
        messageToUser = MessageToUser(parentActivity,null)
    }

    open fun updateMessageDialog(message:String,callback:(args:Any?)->Unit){
        messageToUser = MessageToUser(parentActivity,null)
        messageToUser.setMessage(message)
        messageToUser.setTwoButtons()
        messageToUser.setPositiveCallback(callback)
    }

    open fun showMessage(){
        messageToUser.showMessage()
    }
}