package com.example.foodhero.global
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.example.foodhero.R


/*
*   ##########################################################################
*                                INTENT
*   ##########################################################################
*
* */

fun Activity.moveToActivity(intent: Intent){
    startActivity(intent)
    finish()
}

/*
*   ##########################################################################
*                                LOG MESSAGE TO CONSOLE
*   ##########################################################################
*
* */
fun logMessage(message:String){
    Log.d("Message",message)
}

fun convertDpToPixel(value : Int):Int{
    return (value* Resources.getSystem().displayMetrics.density).toInt()
}

/*
*   ##########################################################################
*
*   ##########################################################################
*
* */
fun templateFunctionAny(parameter:Any?):Unit{}