package com.example.foodhero.struct

import com.example.foodhero.global.SortOperation

class MenuItem(
    val name:String?=null,
    val price:Double?=null,
    val description:String?=null,
    val cathegorie:String? = null,
    val userRating:Double?=null,
    val userComments:ArrayList<String>? = null,
    val menuItemId:String?=null,
    val loggoDownloadUrl:String?=null,):java.io.Serializable {

    fun getIngredients():String{
        return "Salt,Peppar\n\n\nSalt,Peppar"
    }

    fun getCurrentPrice():String{
        return "$price kr"
    }

    override fun toString(): String {
        return "\n" +
                "Name: $name\n" +
                "Price: $price\n" +
                "Description: $description\n" +
                "Category: $cathegorie\n" +
                "UserRating: $userRating\n" +
                "UserComment" + userComments.toString() + "\n" +
                "MenuItemId: $menuItemId\n" +
                "LoggoDownloadUrl: $loggoDownloadUrl\n"}

    fun compare(sortOperation:SortOperation):Double{
        //implement accordingly
        return 0.0
        //return if (sortOperation == SortOperation.SORT_PRICE) price!! else name!![0].code.toDouble()
    }
}