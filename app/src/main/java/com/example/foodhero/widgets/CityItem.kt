package com.example.foodhero.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.foodhero.R


@SuppressLint("ViewConstructor")
class CityItem(val city:String,
               var isActive:Boolean,
               val callbackUnCheck:(args:String)->Unit,
                context: Context?,
                attrs: AttributeSet?): LinearLayout(context,attrs) {

    init{
        LinearLayout.inflate(context, R.layout.city_card,this)
        val cityIsChecked: AppCompatImageView = this.findViewById(R.id.cityIsChecked)
        val cityLbl: TextView = this.findViewById(R.id.cityLbl)

        cityLbl.text = city
        cityIsChecked.visibility = isVisible()

        this.setOnClickListener{
            if(!isActive){
                isActive = true
                cityIsChecked.visibility = VISIBLE
                callbackUnCheck(city)
            }
        }
    }

    private fun isVisible():Int{
        return if(isActive)VISIBLE else GONE
    }

    fun unCheckMe(){
        isActive = false
        val cityIsChecked: AppCompatImageView = this.findViewById(R.id.cityIsChecked)
        cityIsChecked.visibility = GONE
    }
}