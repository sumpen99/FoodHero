package com.example.foodhero.struct

class Edit {
    var c1 = '\u0000'
    var c2 = '\u0000'
    var n = 0
    var next: Edit? = null

    companion object {
        var col = 0
        var row = 0
        fun getIndex(row: Int, col: Int): Int {
            return row * Companion.col + col
        }

        fun setRowCol(row: Int, col: Int) {
            //var cnt = 0
            //val maxCount = row * col
            Companion.row = row
            Companion.col = col
            //while (cnt < maxCount) {
                //edits[cnt++] = Edit()
            //}
        }
    }
}