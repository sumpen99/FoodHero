package com.example.foodhero
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.foodhero.activity.LoginActivity
import com.example.foodhero.activity.OrderActivity
import com.example.foodhero.activity.ProfilActivity
import com.example.foodhero.adapter.RestaurantAdapter
import com.example.foodhero.adapter.RestaurantMenuAdapter
import com.example.foodhero.database.AuthRepo
import com.example.foodhero.database.FirestoreViewModel
import com.example.foodhero.databinding.ActivityMainBinding
import com.example.foodhero.fragment.HomeFragment
import com.example.foodhero.global.*
import com.example.foodhero.struct.FoodHeroInfo
import com.example.foodhero.struct.SearchHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.storage.StorageReference
import org.checkerframework.checker.units.qual.m

class MainActivity : AppCompatActivity() {
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var permissionsList: ArrayList<String>
    private var permissionDialogIsOpen:Boolean = false
    private lateinit var bottomNavMenu: BottomNavigationView
    private var foodHeroInfo = FoodHeroInfo()
    private var permissionsCount = 0
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val auth = AuthRepo()
    private var currentFragment:FragmentInstance? = null

    private var permissionsStr = arrayOf<String>(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    private var permissionsLauncher =
        registerForActivityResult<Array<String>, Map<String, Boolean>>(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String, Boolean>?> {
                val list: ArrayList<Boolean> = ArrayList(it.values)
                permissionsList = ArrayList()
                permissionsCount = 0
                for (i in 0 until list.size) {
                    if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                        permissionsList.add(permissionsStr[i])
                    } else if(!hasPermission(this, permissionsStr[i])) {
                        permissionsCount++
                    }
                }
                if (permissionsList.size > 0) {
                    askForPermissions(permissionsList)
                } else if (permissionsCount > 0) {
                    showPermissionDialog()
                    setLocationOfChoice(false)
                    navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
                }else{
                    setLocationOfChoice(true)
                    navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
                }
            })
    /*
    * remove this comment
    * remove this too
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.isUserLoggedIn()){
            setContentView(R.layout.activity_main)
            setViewModel()
            setDataBinding()
            setBottomNavigationMenu()
            setOnBackNavigation()
            loadListOfCitiesWhereFoodHeroExist()
            launchPermissionRequest()
            Toast.makeText(applicationContext, "Välkommen tillbaka ${auth.getEmail()}.", Toast.LENGTH_SHORT).show()
        }
        else{
            moveToActivityAndFinish(Intent(this,LoginActivity::class.java))
        }
    }


    /*
    *   ##########################################################################
    *               SET BINDING AND OTHER STUFF
    *   ##########################################################################
    */

    private fun setDataBinding(){
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setViewModel(){
        firestoreViewModel = FirestoreViewModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBottomNavigationMenu(){
        bottomNavMenu = binding.bottomNavigationView
        bottomNavMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                R.id.navigateHome->(FragmentInstance.FRAGMENT_MAIN_HOME)
              //   R.id.navigateSearch->moveToActivityAndPutOnTop(Intent(this,OrderActivity::class.java))
                R.id.navigateCart->moveToActivityAndPutOnTop(Intent(this,OrderActivity::class.java))
                R.id.navigateProfile->moveToActivityAndPutOnTop(Intent(this, ProfilActivity::class.java))

            }
            true
        }
    }

    /*
    *   ##########################################################################
    *               PERMISSION CHECK
    *   ##########################################################################
    */

    private fun launchPermissionRequest(){
        permissionsList = ArrayList()
        permissionsList.addAll(permissionsStr)
        askForPermissions(permissionsList)
    }

    private fun askForPermissions(permissionsList: ArrayList<String>) {
        val newPermissionStr:Array<String> = permissionsList.toTypedArray()
        if(newPermissionStr.isNotEmpty()) {
            permissionsLauncher.launch(newPermissionStr)
        }
        else if(!permissionDialogIsOpen) {
            permissionDialogIsOpen = true
            showPermissionDialog()
        }
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr) == PackageManager.PERMISSION_GRANTED
    }

    /*
    *   ##########################################################################
    *               SET NAVIGATION IF USER DRAGS LEFT OR RIGHT SIDE OF SCREEN
    *   ##########################################################################
    */

    private fun setOnBackNavigation(){
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed(){navigateOnBackPressed()}
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
    }
    
    private fun navigateOnBackPressed(){
        logMessage(supportFragmentManager.fragments.toString())
    }

    /*
    *   ##########################################################################
    *               NAVIGATE BETWEEN FRAGMENTS
    *   ##########################################################################
    */

    private fun navigateToFragment(fragment: FragmentInstance){
        if(isSameFragment(fragment))return
        currentFragment = fragment
        when(fragment){
            FragmentInstance.FRAGMENT_MAIN_HOME->applyTransaction(HomeFragment(intent))
            else -> {}
        }
    }

    private fun applyTransaction(frag: Fragment){
        supportFragmentManager.beginTransaction().apply {
            add(R.id.homeLayout,frag).commit()
        }
    }

    private fun isSameFragment(fragmentInstance:FragmentInstance):Boolean{
        currentFragment?:return false
        return currentFragment == fragmentInstance
    }

    /*
    *   ##########################################################################
    *               NAVIGATE BASED ON CURRENT USER STATUS
    *   ##########################################################################
    */

    private fun navigateOnResume(){
        if(!auth.isUserLoggedIn()){
            moveToActivityAndFinish(Intent(this,LoginActivity::class.java))
        }
        else{
            navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
        }


    }

    /*
    *   ##########################################################################
    *               SHARED PREFERENCE
    *   ##########################################################################
    */

    fun setLocationOfChoice(location:Boolean){
        writeBooleanToSharedPreference(userLocationTag(),location)
    }

    fun setCityOfChoice(city:String){
        writeStringToSharedPreference(userCityTag(),city)
    }

    private fun userLocationTag():String{
        return auth.userUid() + getString(R.string.user_location)
    }

    private fun userCityTag():String{
        return auth.userUid() + getString(R.string.user_city)
    }

    fun getCityOfChoice():String{
        val city = retrieveStringFromSharedPreference(userCityTag(),"")?:""
        val geo = getLocationOfChoice()
        if(city == "" && geo)return getString(R.string.user_current_location_geo)
        else if(city == "")return getString(R.string.user_current_location_set)
        return city
    }

    private fun cityNotSetByUser():Boolean{
        return getCityOfChoice() == getString(R.string.user_current_location_geo)
    }

    fun getCheckMarkerVisibility():Int{
        return if(getCityOfChoice() == getString(R.string.user_current_location_geo))VISIBLE else GONE
    }

    private fun getLocationOfChoice():Boolean{
        return retrieveBooleanFromSharedPreference(userLocationTag(),false)?:false
    }

    private fun isACorrectCity(city:String):Boolean{
        return  city != "" &&
                city != getString(R.string.user_current_location_geo) &&
                city != getString(R.string.user_current_location_set)
    }

    /*
    *   ##########################################################################
    *               COLLECT RESTAURANTS & MENU
    *   ##########################################################################
    */

    fun getRestaurantLoggoRef(downloadUrl:String?): StorageReference {
        return firestoreViewModel.firebaseRepository.getRestaurantLoggoReference(downloadUrl)
    }

    fun getRestaurantMenuItemLoggoRef(downloadUrl:String?): StorageReference {
        return firestoreViewModel.firebaseRepository.getMenuItemLoggoReference(downloadUrl)
    }

    fun getCitiesWhereFoodHeroExist():FoodHeroInfo{
        return foodHeroInfo
    }

    fun loadRestaurants(restaurantAdapter:RestaurantAdapter){
        if(getLocationOfChoice() &&
            cityNotSetByUser()){
            val userLocation = getCenterOfStockholm()
            val radiusKm = 20.0
            firestoreViewModel.getRestaurantsGeo(userLocation,radiusKm,restaurantAdapter)
        }
        else{
            val city = getCityOfChoice()
            if(isACorrectCity(city)){
                loadRestaurantsByCity(city,restaurantAdapter)
            }
        }
    }

    fun loadRestaurantMenu(restaurantId:String,restaurantMenuAdapter: RestaurantMenuAdapter){
        firestoreViewModel.getMenuItems(restaurantId,restaurantMenuAdapter)

    }

    private fun loadRestaurantsByCity(city:String,restaurantAdapter: RestaurantAdapter){
        firestoreViewModel.getRestaurantsByCity(city,restaurantAdapter)
    }

    fun loadRestaurantsByKeyWord(idList:ArrayList<String>,keyWord:String,restaurantAdapter: RestaurantAdapter){
        firestoreViewModel.getRestaurantsByKeyWord(idList,keyWord,restaurantAdapter)
    }

    fun loadRestaurantsByCathegory(ids:List<String>,restaurantAdapter: RestaurantAdapter){
        firestoreViewModel.getRestaurantsByIds(ids,restaurantAdapter)
    }

    private fun loadListOfCitiesWhereFoodHeroExist(){
        firestoreViewModel.getCitiesWhereFoodHeroExist(foodHeroInfo)
    }


    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */
    /*override fun onResume(){
        super.onResume()
        //navigateOnResume()
        //logMessage("on resume main")

    }

    override fun onPause(){
        super.onPause()
        //logMessage("on pause main")

    }

    override fun onStop(){
        super.onStop()
        //logMessage("on stop main")

    }

    override fun onDestroy(){
        super.onDestroy()
    }*/
}
