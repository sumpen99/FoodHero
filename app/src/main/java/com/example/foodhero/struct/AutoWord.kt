package com.example.foodhero.struct

class AutoWord(var index:Int,var edits:Int,var word:String) {

    fun swapValues(index:Int,edits:Int,word:String){
        this.index = index
        this.edits = edits
        this.word = word
    }
}