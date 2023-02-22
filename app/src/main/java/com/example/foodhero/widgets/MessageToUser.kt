package com.example.foodhero.widgets

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.example.foodhero.R

class MessageToUser(val context: Context, val view: View?){
    private var positiveCallback:((args:Any?)->Unit) ? = null
    private var neutralCallback:((args:Any?)->Unit) ? = null
    private var negativeCallback:((args:Any?)->Unit) ? = null
    private var oneButton:Boolean = true
    private var callbackArgs:Any? = null
    private var message:String = ""
    private var isOpen:Boolean = false
    private var posBtnText:String = "OK"
    private var negBtnText:String = "Cancel"

    private fun setCallbackArgs(args:Any?){
        callbackArgs = args
    }

    fun setPositiveCallback(callback:(args:Any?)->Unit){
        positiveCallback = callback
    }

    fun setNeutralCallback(callback:(args:Any?)->Unit){
        neutralCallback = callback
    }

    fun setNegativeCallback(callback:(args:Any?)->Unit){
        negativeCallback = callback
    }

    fun setYesNoBtn(yesNo:Boolean){
        posBtnText = "Yes"
        negBtnText = "No"
    }

    fun setPosBtnText(lbl:String){
        posBtnText = lbl
    }

    fun setTwoButtons(){
        oneButton = false
    }

    fun setMessage(msg:String){
        message = msg
    }

    private fun setStatusOpen(value:Boolean){
        isOpen = value
    }

    fun showMessage(){
        if(!isOpen){
            setStatusOpen(true)
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val positiveButtonClick = { dialog: DialogInterface, which:Int->
                if(positiveCallback!=null){ positiveCallback!!(callbackArgs)}
                setStatusOpen(false)
            }
            val negativeButtonClick = { dialog: DialogInterface, which:Int->
                if(negativeCallback!=null){negativeCallback!!(callbackArgs)}
                setStatusOpen(false)
            }
            builder.setTitle(message)
            builder.setPositiveButton(posBtnText, DialogInterface.OnClickListener(positiveButtonClick))
            if(!oneButton){
                builder.setNegativeButton(negBtnText, DialogInterface.OnClickListener(negativeButtonClick))
            }
            builder.show().setCanceledOnTouchOutside(false)
        }
    }

    fun showLoginRequiredMessage(){
        if(!isOpen){
            setStatusOpen(true)
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val signUpButtonClick = { dialog: DialogInterface, which:Int->
                positiveCallback!!(null)
                setStatusOpen(false)
            }
            val logInButtonClick = {dialog: DialogInterface, which:Int->
                neutralCallback!!(null)
                setStatusOpen(false)}

            val cancelButtonClick = { dialog: DialogInterface, which:Int->
                setStatusOpen(false)
            }
            builder.setTitle("Hoppsan Kerstin")
            builder.setMessage("För att fortsätta behöver ni vara inloggade\n")
            builder.setPositiveButton("Skapa Konto", DialogInterface.OnClickListener(signUpButtonClick))
            builder.setNegativeButton("Logga In",DialogInterface.OnClickListener(logInButtonClick))
            builder.setNeutralButton("Avbryt", DialogInterface.OnClickListener(cancelButtonClick))
            builder.show().setCanceledOnTouchOutside(false)
        }
    }

}