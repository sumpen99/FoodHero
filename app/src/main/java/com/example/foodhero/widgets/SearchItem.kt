package com.example.foodhero.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.example.foodhero.R

@SuppressLint("ViewConstructor")
class SearchItem(val keyWord:String,
               val callbackSearch:(args:String)->Unit,
               context: Context?,
               attrs: AttributeSet?): LinearLayout(context,attrs) {

    init{
        LinearLayout.inflate(context, R.layout.search_card,this)
        val keyWordLbl: TextView = this.findViewById(R.id.keyWordTextView)

        keyWordLbl.text = keyWord

        this.setOnClickListener{
            callbackSearch(keyWord)
        }

    }
}