package com.example.foodhero.struct

class AutoWordContainer(val bufSize:Int) {
    var wordCount = 0
    val bufWords = arrayOfNulls<AutoWord>(bufSize)

    fun insertWord(autoWord:AutoWord){
        bufWords[wordCount++] = autoWord
    }

    fun getWord(index:Int):String{
        return bufWords[index]?.word?:""
    }
}