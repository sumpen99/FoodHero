package com.example.foodhero.struct

class FoodHeroInfo(
    var cities:ArrayList<String>? = null,
) {

    fun sortListOfCities(){
        cities?.sort()
    }

}