package com.example.foodhero.struct

import com.example.foodhero.global.ServerResult

class ServerDetails(var pos:Int, var msg:String,var serverResult:ServerResult){
    constructor():this(0,"", ServerResult.UPLOAD_OK)
}