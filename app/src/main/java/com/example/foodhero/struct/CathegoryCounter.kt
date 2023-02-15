package com.example.foodhero.struct

class CathegoryCounter() {
    var sumOfItems = 0
    var listOfIds = ArrayList<String>()

    fun updateCounter(id:String){
        appendNewId(id)
        increaseIndex()
    }

    private fun appendNewId(id:String){
        listOfIds.add(id)
    }

    private fun increaseIndex(){
        sumOfItems++
    }
}