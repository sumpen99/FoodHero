package com.example.foodhero.struct

class FavoriteItem (
    val itemId: String? =null,
    val foodName: String? = null,
    val restaurantName: String? = null,


    ) : java.io.Serializable {

    override fun toString(): String {
        return "\n" +
                "itemID: $itemId\n"+
                "Name: $foodName\n" +
                "Price: $restaurantName\n"
    }
}