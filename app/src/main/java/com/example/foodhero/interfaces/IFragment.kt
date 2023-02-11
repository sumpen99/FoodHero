package com.example.foodhero.interfaces
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.foodhero.global.FragmentInstance

interface IFragment {
    fun getFragmentID(): FragmentInstance
    fun setFragmentBinding(inflater: LayoutInflater, container: ViewGroup?,)
    fun setFragmentView()
    fun parentFragment():FragmentInstance?
}