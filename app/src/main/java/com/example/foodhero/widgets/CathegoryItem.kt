package com.example.foodhero.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.foodhero.R
import com.example.foodhero.global.logMessage

@SuppressLint("ViewConstructor")
class CathegoryItem(val cat:String,
                    val count:Int,
                    context: Context?,
                    attrs: AttributeSet?): LinearLayout(context,attrs) {
    init{
        LinearLayout.inflate(context, R.layout.cathegory_card,this)
        val img:AppCompatImageView = this.findViewById(R.id.catFoodImg)
        val catLbl:TextView = this.findViewById(R.id.catFoodCat)
        val catCount:TextView = this.findViewById(R.id.catFoodCount)
        img.setImageResource(getImageBasedOnCat(cat))
        catLbl.text = getCatBasedOnCat(cat)
        catCount.text = getCatCount(count)

        this.setOnClickListener{
            logMessage("hepp")
        }

    }

    private fun getImageBasedOnCat(cat:String):Int{
        when(cat){
            "Asian"->return R.drawable.cat_asian
            "Burgers"->return R.drawable.cat_burgers
            "Fish"->return R.drawable.cat_fish
            "Meat"->return R.drawable.cat_meat
            "Pizza"->return R.drawable.cat_pizza
        }

        return R.drawable.ic_image_error_foreground
    }

    private fun getCatBasedOnCat(cat:String):String{
        when(cat){
            "Asian"->return "Asiatisk"
            "Burgers"->return "Hamburgare"
            "Fish"->return "Fisk"
            "Meat"->return "Kött"
            "Pizza"->return "Pizza"
        }

        return "Undefined"
    }

    private fun getCatCount(count:Int):String{
        return "$count st"
    }

}