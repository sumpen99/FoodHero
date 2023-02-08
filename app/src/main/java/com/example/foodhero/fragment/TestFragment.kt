package com.example.foodhero.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.foodhero.R
import com.example.foodhero.databinding.FragmentTestBinding
import com.example.foodhero.global.hideKeyboard

class TestFragment:Fragment(R.layout.fragment_test) {
    private var _binding: FragmentTestBinding? = null
    private var baseView: View? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentTestBinding.inflate(inflater,container,false)
        baseView = binding.root
        //setEventListener(baseView!!)
        return baseView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setEventListener(baseView!!)
    }

    fun testFunc(){
        val x = binding.userEmail

    }

    private fun setEventListener(view: View){

        /*view.setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_DOWN -> {emailField.hideKeyboard();passwordField.hideKeyboard()}
            }
            true
        }*/
    }
}