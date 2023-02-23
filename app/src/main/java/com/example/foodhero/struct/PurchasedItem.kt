package com.example.foodhero.struct

import com.google.firebase.firestore.DocumentId

class PurchasedItem (
    val itemId: String? =null,
    val foodName: String? = null,
    val price: Double? = null,


) : java.io.Serializable {

    override fun toString(): String {
        return "\n" +
                "itemID: $itemId\n"+
                "Name: $foodName\n" +
                "Price: $price\n"
    }
}

