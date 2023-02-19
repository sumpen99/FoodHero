package com.example.foodhero.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.foodhero.R
import com.example.foodhero.global.logMessage
import com.example.foodhero.struct.CathegoryCounter

@SuppressLint("ViewConstructor")
class CathegoryItem(cat:String,
                    catCounter:CathegoryCounter,
                    sortFunc:(args:List<String>)->Unit,
                    context: Context?,
                    attrs: AttributeSet?): LinearLayout(context,attrs) {
    init{
        LinearLayout.inflate(context, R.layout.cathegory_card,this)
        val img:AppCompatImageView = this.findViewById(R.id.catFoodImg)
        val catLbl:TextView = this.findViewById(R.id.catFoodCat)
        val catCount:TextView = this.findViewById(R.id.catFoodCount)
        img.setImageResource(getImageBasedOnCat(cat))
        catLbl.text = getCatBasedOnCat(cat)
        catCount.text = getCatCount(catCounter.sumOfItems)

        this.setOnClickListener{
            sortFunc(catCounter.listOfIds)
        }

    }

    private fun getImageBasedOnCat(cat:String):Int{
        when(cat){
            "Asian"->return R.drawable.cat_asian
            "Burgers"->return R.drawable.cat_burgers
            "Fish"->return R.drawable.cat_fish
            "Meat"->return R.drawable.cat_meat
            "Pizza"->return R.drawable.cat_pizza
            "Visa Alla"->return R.drawable.applogoblack
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
            "Visa Alla"->return "Visa Alla"
        }

        return "Undefined"
    }

    private fun getCatCount(count:Int):String{
        return "$count st"
    }

}