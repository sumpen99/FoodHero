package com.example.foodhero.global
import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.location.Location
import android.location.LocationManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.foodhero.R
import com.example.foodhero.struct.AutoWord
import com.example.foodhero.struct.AutoWordContainer
import com.example.foodhero.struct.Edit
import com.firebase.geofire.GeoLocation
import com.google.firebase.storage.StorageReference
import java.util.*


/*
*   ##########################################################################
*                               PERMISSION DIALOG
*   ##########################################################################
*
* */
fun Activity.showPermissionDialog() {
    val alertDialog: AlertDialog
    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
    builder.setTitle("Permission required")
        .setMessage("Application requires users`s location for better user experience")
        .setPositiveButton("Settings") { dialog, which -> dialog.dismiss() }
    alertDialog = builder.create()
    alertDialog.show()
}

/*
*   ##########################################################################
*                            SHARED PREFERENCE
*   ##########################################################################
*
* */

fun Activity.writeStringToSharedPreference(tag:String,value:String):Boolean{
    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?:return false
    with(sharedPref.edit()){
        putString(tag,value)
        apply()
    }
    return true
}

fun Activity.writeBooleanToSharedPreference(tag:String,value:Boolean):Boolean{
    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?:return false
    with(sharedPref.edit()){
        putBoolean(tag,value)
        apply()
    }
    return true
}

fun Activity.retrieveStringFromSharedPreference(tag:String,default:String?=null):String?{
    val def = default?:""
    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?:return null
    return sharedPref.getString(tag,def)
}

fun Activity.retrieveBooleanFromSharedPreference(tag:String,default:Boolean?=null):Boolean?{
    val def = default?:false
    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?:return null
    return sharedPref.getBoolean(tag,def)
}

/*
*   ##########################################################################
*                                LOAD IMAGES FROM STORAGE
*   ##########################################################################
*
* */
fun Activity.downloadImageFromStorage(storeRef: StorageReference, imageView: ImageView){
    val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
    GlideApp.with(this)
        .load(storeRef)
        .error(R.drawable.ic_image_error_foreground)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .into(imageView)
}


/*
*   ##########################################################################
*                                INTENT
*   ##########################################################################
*
* */

fun Activity.moveToActivityAndPutOnTop(intent: Intent){
    startActivity(intent)
    intent.flags = FLAG_ACTIVITY_PREVIOUS_IS_TOP
}

fun Activity.moveToActivityAndFinish(intent: Intent){
    startActivity(intent)
    finish()
}

fun Activity.moveToActivityAndClearTop(){
    val broadcastIntent = Intent()
    broadcastIntent.action = APP_ACTION_LOG_OUT
    sendBroadcast(broadcastIntent)
    finish()
}

/*
*   ##########################################################################
*                            TOAST MESSAGE
*   ##########################################################################
*
* */

fun Activity.showMessage(msg:String,duration:Int){
    Toast.makeText(this,msg,duration).show()
}

/*
*   ##########################################################################
*                            GET USER LOCATION
*   ##########################################################################
*
* */

fun Activity.locationPermissionIsProvided():Boolean {
    return (checkGpsProviderStatus() &&
            ContextCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED)
}

fun Activity.getUserLocation(): GeoLocation {
    val location: Location?
    if(checkGpsProviderStatus() &&
        ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ){
        location =  (getSystemService(Context.LOCATION_SERVICE) as LocationManager).getLastKnownLocation(
            LocationManager.GPS_PROVIDER)
        if(location!=null){return GeoLocation(location.latitude,location.longitude)}
    }
    return getCenterOfStockholm()
}

fun Activity.checkGpsProviderStatus():Boolean{
    return (getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun getCenterOfStockholm(): GeoLocation {
    return GeoLocation(59.332911,18.054698)
}

/*
*   ##########################################################################
*                                EDIT TEXTVIEW
*   ##########################################################################
*
* */

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
    clearFocus()
}

/*
*   ##########################################################################
*                                CLICK EFFECT
*   ##########################################################################
*
* */

@SuppressLint("ClickableViewAccessibility")
fun View.clickEffect(){
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.background.colorFilter = LightingColorFilter(Color.WHITE,Color.LTGRAY)
                v.invalidate()
            }
            MotionEvent.ACTION_UP -> {
                v.background.clearColorFilter()
                v.invalidate()
            }
        }
        false
    }
}

/*
*   ##########################################################################
*                                CLEAR CHILDREN
*   ##########################################################################
*
* */

fun ViewGroup.clearChildren(childrenToNotRemove:Int){
    while(childCount>childrenToNotRemove){
        var i = childrenToNotRemove
        val childCount = childCount
        while(i<childCount){
            removeView(getChildAt(i))
            i++
        }
    }
}

/*
*   ##########################################################################
*                                EDIT TEXT
*   ##########################################################################
*
* */

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

/*
*   ##########################################################################
*                                LOG MESSAGE TO CONSOLE
*   ##########################################################################
*
* */
fun logMessage(message:String){
    Log.d("Message",message)
}

fun convertDpToPixel(value : Int):Int{
    return (value* Resources.getSystem().displayMetrics.density).toInt()
}

fun getRandomNumber(maxSize:Int,minValue:Double):Double{
    val rnd:Int = ((Math.random()*100000000)%maxSize).toInt()
    return rnd+minValue
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun String.capitalizeSentence() = run {
    val words = this.split(" ")
    var output = ""
    for(word in words){
        output += word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } + " "
    }
    output.trim()
}

/*
*   ##########################################################################
*                           LEVENSTEIN
*   ##########################################################################
*
* */
fun checkForSuggestion(strInput: String, wordsOut: MutableList<String>,wordsToTest:ArrayList<String>): Boolean {
    fun offsetString(str:String,offset:Int,EMPTY_CHAR:Char):Char{
        if(offset >= str.length){return EMPTY_CHAR;}
        return str[offset]
    }
    if(strInput.isEmpty() || wordsToTest.isEmpty())return false
    val EMPTY_CHAR = '\u0000'
    val la = strInput.length
    var lb: Int = 0
    var tbl: Array<Edit>
    val strIn = strInput.capitalizeSentence()
    val aContainer = AutoWordContainer(wordsToTest.size)
    for((k,strOut) in wordsToTest.withIndex()) {
        lb = strOut.length
        val row = la + 2
        val col = lb + 2
        tbl = Array(row*col){Edit()}
        Edit.setRowCol(row, col)
        for(i in la downTo 0) {
            val aa: Char = offsetString(strIn, i, EMPTY_CHAR)
            for(j in lb downTo  0) {
                val bb: Char = offsetString(strOut, j, EMPTY_CHAR)
                if (aa == EMPTY_CHAR && bb == EMPTY_CHAR){continue}
                val e: Edit = tbl[Edit.getIndex(i, j)]
                val repl: Edit = tbl[Edit.getIndex(i + 1, j + 1)]
                val dela: Edit = tbl[Edit.getIndex(i + 1, j)]
                val delb: Edit = tbl[Edit.getIndex(i, j + 1)]
                e.c1 = aa
                e.c2 = bb
                if(aa == EMPTY_CHAR) {
                    e.next = delb
                    e.n = e.next!!.n + 1
                    continue
                }
                if (bb == EMPTY_CHAR) {
                    e.next = dela
                    e.n = e.next!!.n + 1
                    continue
                }
                e.next = repl
                if (aa == bb) {
                    e.n = e.next!!.n
                    continue
                }
                if (e.next!!.n > delb.n) {
                    e.next = delb
                    e.c1 = EMPTY_CHAR
                }
                if (e.next!!.n > dela.n) {
                    e.next = dela
                    e.c1 = aa
                    e.c2 = EMPTY_CHAR
                }
                e.n = e.next!!.n + 1
            }
        }
        if (tbl[0].n == 0) {
            return false
        }
        aContainer.insertWord(AutoWord(k,tbl[0].n,strOut))
    }
    sortAutoWordContainer(aContainer,0,aContainer.wordCount - 1)
    for(l in 0 until wordsOut.size) {
        wordsOut[l] = aContainer.getWord(l)
    }
    return true
}
fun sortAutoWordContainer(aContainer: AutoWordContainer,low: Int, high: Int) {
    sortWordList(aContainer.bufWords, low, high)
}

fun sortWordList(wordList: Array<AutoWord?>,low: Int, high: Int) {
    if (low < high) {
        val q: Int = partitionWordList(wordList, low, high)
        sortWordList(wordList, low, q)
        sortWordList(wordList, q + 1, high)
    }
}

fun partitionWordList(wordList: Array<AutoWord?>, low: Int, high: Int):Int {
    val pivot = wordList[low]!!.edits
    var i = low - 1
    var j = high + 1
    while (true) {
        while (++i < high && wordList[i]!!.edits < pivot);
        while (--j > low && wordList[j]!!.edits > pivot);
        if (i < j) {
            swapAutoWord(wordList[i]!!, wordList[j]!!)
        } else {
            return j
        }
    }
}

fun swapAutoWord(w1: AutoWord, w2: AutoWord) {
    val index = w1.index
    val edits = w1.edits
    val word = w1.word
    w1.swapValues(w2.index, w2.edits, w2.word)
    w2.swapValues(index, edits, word)
}

/*
*   ##########################################################################
*
*   ##########################################################################
*
* */
fun templateFunctionAny(parameter:Any?):Unit{}