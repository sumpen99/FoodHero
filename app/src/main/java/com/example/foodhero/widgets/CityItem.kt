package com.example.foodhero.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.foodhero.R


@SuppressLint("ViewConstructor")
class CityItem(city:String,
                    context: Context?,
                    attrs: AttributeSet?): LinearLayout(context,attrs) {
    init{
        LinearLayout.inflate(context, R.layout.city_card,this)
        val cityIsChecked: AppCompatImageView = this.findViewById(R.id.cityIsChecked)
        val cityLbl: TextView = this.findViewById(R.id.cityLbl)

        cityLbl.text = city

        this.setOnClickListener{
            cityIsChecked.visibility = if(cityIsChecked.visibility == GONE)VISIBLE else GONE
        }

    }
}