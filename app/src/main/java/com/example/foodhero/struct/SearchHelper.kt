package com.example.foodhero.struct

import com.example.foodhero.global.SEARCH_TIME_OUT
import com.example.foodhero.global.SearchQuery
import com.firebase.geofire.GeoLocation

class SearchHelper {
    var lastQuery:SearchQuery = SearchQuery.QUERY_FIRST_RUN
    var lastKeyWord:String = ""
    var lastSearchTime:Long = 0L
    var foundResult:Boolean = false

    fun searchTimeOut():Boolean{
        val time = System.currentTimeMillis()
        return (time-lastSearchTime < SEARCH_TIME_OUT)
    }

    fun newQuery(key:SearchQuery,keyWord:Any?):Boolean{
        if(searchTimeOut())return false
        when(key){
            SearchQuery.QUERY_GEO->{
                val geoPosition = keyWord as GeoLocation
            }
            SearchQuery.QUERY_CITY->{
                val city = keyWord as String
            }
            SearchQuery.QUERY_CATHEGORY->{

            }
            SearchQuery.QUERY_REFRESH->{
                lastSearchTime = System.currentTimeMillis()
                return true
            }
            else ->{}
        }
        return true
    }
}