package com.example.foodhero.struct

class CathegoryCounter() {
    var sumOfItems = 0
    var listOfIds = ArrayList<String>()

    fun updateCounter(id:String){
        appendNewId(id)
        increaseIndex()
    }

    fun addIdList(idList:ArrayList<String>){
        listOfIds.addAll(idList)
        sumOfItems+=idList.size
    }

    fun resetValues(){
        sumOfItems = 0
        listOfIds.clear()
    }

    private fun appendNewId(id:String){
        listOfIds.add(id)
    }

    private fun increaseIndex(){
        sumOfItems++
    }

}