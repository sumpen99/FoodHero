package com.example.foodhero.widgets
import android.app.Dialog
import android.content.Context
import com.example.foodhero.global.DialogInstance

class BottomSheetDialog(
    context: Context,
    themeResId:Int,
    val dialogInstance: DialogInstance):
    Dialog(context,themeResId) {
        var lastId = ""

    override fun show() {
        super.show()
    }
}