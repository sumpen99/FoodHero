package com.example.foodhero.struct

import com.example.foodhero.global.SortOperation


class Restaurant(
    val country:String?=null,
    val name:String?=null,
    val city:String?=null,
    val orgNumber:String?=null,
    val adress:String?=null,
    val phonenumber:String?=null,
    val email:String?=null,
    val cathegoriesDishes:ArrayList<String>? = null,
    val keyWords:ArrayList<String>? = null,
    val description:String?=null,
    val geohash:String?=null,
    val lat:Double?=null,
    val lon:Double?=null,
    val userRating:Double?=null,
    val userComments:ArrayList<String>? = null,
    val deliveryRange:Double?=null,
    val deliveryCost:Double?=null,
    val deliveryMethods:ArrayList<String>? = null,
    val restaurantId:String?=null,
    val loggoDownloadUrl:String?=null,):java.io.Serializable {

    

    fun getOpeningHours():String{
        return "06.00 - 22.00"
    }

    fun getUserRatingString():String{
        return if(userRating!! > 7.0) " Very Good, $userRating"
            else "$userRating"
    }

    fun getDeliveryCostString():String{
        return "50 kr"
    }

    fun getDeliveryTime():String{
        return "15 - 30 min"
    }

    fun getRestaurantInfo():String{
        return "No Info Yet\n\n\n"
    }

    fun getCategoriesString():String{
        cathegoriesDishes?:return ""
        var out = ""
        for(cat:String in cathegoriesDishes){
            out+= "$cat "
        }
        return out
    }

    override fun toString(): String {
        return "\n" +
                "Country: : $country\n" +
                "Name: $name\n" +
                "City: $city\n" +
                "Orgnum: $orgNumber\n" +
                "Address: $adress\n" +
                "PhoneNumber: $phonenumber\n" +
                "Email: $email\n" +
                "Categories: " + cathegoriesDishes.toString() + "\n" +
                "KeyWords: " + keyWords.toString() + "\n" +
                "Description: $description\n" +
                "GeoHash: $geohash\n" +
                "Lat: $lat\n" +
                "Lon: $lon\n" +
                "UserRating: $userRating\n" +
                "UserComments: " + userComments.toString() + "\n" +
                "DeliveryRange: $deliveryRange\n" +
                "DeliveryCost: $deliveryCost\n" +
                "DeliveryMethods: " +deliveryMethods.toString() + "\n" +
                "RestaurantId: $restaurantId\n" +
                "LoggoDownloadUrl: $loggoDownloadUrl\n"}

    fun compare(sortOperation: SortOperation):Double{
        //implement accordingly
        return 0.0
        //return if (sortOperation == SortOperation.SORT_PRICE) price!! else name!![0].code.toDouble()
    }
}