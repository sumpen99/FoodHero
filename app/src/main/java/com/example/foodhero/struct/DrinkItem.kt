package com.example.foodhero.struct

class DrinkItem (
    val name:String?=null,
    val price:Double?=null,
    val volume:Double?=null,
    val drinkItemId:String?=null,
   ):java.io.Serializable {

        override fun toString(): String {
            return  "\n" +
                    "Name: $name\n" +
                    "Price: $price\n" +
                    "Volume: $volume\n" +
                    "DrinkItemId: $drinkItemId\n"}
}