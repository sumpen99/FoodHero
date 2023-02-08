package com.example.foodhero
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.foodhero.activity.LoginActivity
import com.example.foodhero.database.AuthRepo
import com.example.foodhero.database.FirestoreViewModel
import com.example.foodhero.databinding.ActivityMainBinding
import com.example.foodhero.fragment.HomeFragment
import com.example.foodhero.global.*
import com.example.foodhero.struct.Restaurant
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var permissionsList: ArrayList<String>
    private var permissionDialogIsOpen:Boolean = false
    private lateinit var bottomNavMenu: BottomNavigationView
    private var savedRestaurantsList : MutableList<Restaurant> = mutableListOf()
    private var permissionsCount = 0
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val auth = AuthRepo()


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
                    // sort restaurants by some default
                    // or let the user pick a city
                    //logMessage("We have not permission")
                }else{
                    //logMessage("We have permission")
                    //loadRestaurantsByUserPosition()
                }
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.isUserLoggedIn()){
            //auth.signOut()
            //return
            setContentView(R.layout.activity_main)
            setViewModel()
            setDataBinding()
            setBottomNavigationMenu()
            setOnBackNavigation()
            launchPermissionRequest()
            navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
            Toast.makeText(applicationContext, "Välkommen tillbaka ${auth.getEmail()}.", Toast.LENGTH_SHORT).show()
        }
        else{
            moveToActivity(Intent(this,LoginActivity::class.java))
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
        firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBottomNavigationMenu(){
        bottomNavMenu = binding.bottomNavigationView
        bottomNavMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                R.id.navigateHome->navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
                //R.id.navigateSearch->SEARCH BOX
                //R.id.navigateCart->navigateFragment(FragmentInstance.FRAGMENT_MAIN_CART)
                //R.id.navigateProfile->navigateFragment(FragmentInstance.FRAGMENT_MAIN_PROFILE)
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

    fun navigateOnBackPressed(){
        logMessage("navigate me")
    }

    /*
    *   ##########################################################################
    *               NAVIGATE BETWEEN FRAGMENTS
    *   ##########################################################################
    */

    private fun navigateToFragment(fragment: FragmentInstance){
        when(fragment){
            FragmentInstance.FRAGMENT_MAIN_HOME->applyTransaction(HomeFragment())
            else -> {}
        }
    }

    private fun applyTransaction(frag: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.homeLayout,frag).commit()
        }
    }

    /*
    *   ##########################################################################
    *               NAVIGATE BASED ON CURRENT USER STATUS
    *   ##########################################################################
    */

    private fun navigateOnResume(){
        if(auth.isUserLoggedIn()){
            moveToActivity(Intent(this,LoginActivity::class.java))
        }
        else{
            navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
        }


    }

    /*
    *   ##########################################################################
    *               NAVIGATE BASED ON CURRENT USER STATUS
    *   ##########################################################################
    */

    private fun loadRestaurantsByUserPosition(){
        //savedRestaurantsList.clear()
        //val userLocation = getUserLocation()
        val userLocation = getCenterOfStockholm()
        val radiusKm = 20.0
        firestoreViewModel.getRestaurantsGeo(userLocation,radiusKm,::testResultOfLoadedRestaurants)
    }

    private fun testResultOfLoadedRestaurants(parameter:Any?){
        savedRestaurantsList = parameter as MutableList<Restaurant>
        for(restaurant in savedRestaurantsList){
            //logMessage(restaurant.toString())
        }
    }

    fun getListOfLoadedRestaurants():List<Restaurant>{
        //firestoreViewModel.getMenuItems(restaurant.Id!!)
        //firestoreViewModel.getDrinkList(restaurant.Id!!)
        for(restaurant in savedRestaurantsList){
            logMessage(restaurant.toString())
        }
        return savedRestaurantsList
    }

    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */
    override fun onResume(){
        super.onResume()
        //navigateOnResume()
        logMessage("on resume main")

    }

    override fun onPause(){
        super.onPause()
        logMessage("on pause main")

    }

    override fun onStop(){
        super.onStop()
        logMessage("on stop main")

    }

    override fun onDestroy(){
        super.onDestroy()
   }
}
