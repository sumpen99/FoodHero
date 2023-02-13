package com.example.foodhero.widgets
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.example.foodhero.R

class CathegoryItem(context: Context?,
                    attrs: AttributeSet?): LinearLayout(context,attrs) {
    init{
        LinearLayout.inflate(context, R.layout.cathegory_card,this)
        //val height = convertDpToPixel((resources.getDimension(R.dimen.bottomMapMenu)).toInt())
        //layoutParams = ViewGroup.LayoutParams(getScreenWidth(),height)
    }

}