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
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.example.foodhero.R
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
*
*   ##########################################################################
*
* */
fun templateFunctionAny(parameter:Any?):Unit{}