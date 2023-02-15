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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var permissionsList: ArrayList<String>
    private var permissionDialogIsOpen:Boolean = false
    private lateinit var bottomNavMenu: BottomNavigationView
    private var permissionsCount = 0
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val auth = AuthRepo()
    private var loadRestaurantsGeo = false
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
                    loadRestaurantsGeo = false
                    navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
                }else{
                    loadRestaurantsGeo = true
                    navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
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
            //navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
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
        firestoreViewModel = FirestoreViewModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBottomNavigationMenu(){
        bottomNavMenu = binding.bottomNavigationView
        bottomNavMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                R.id.navigateHome->navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
                //R.id.navigateSearch->SEARCH BOX
                R.id.navigateCart->moveToActivity(Intent(this,OrderActivity::class.java))
                R.id.navigateProfile->moveToActivity(Intent(this, ProfilActivity::class.java))

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
            replace(R.id.homeLayout,frag).commit()
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
        if(auth.isUserLoggedIn()){
            moveToActivity(Intent(this,LoginActivity::class.java))
        }
        else{
            navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
        }


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

    fun loadRestaurants(restaurantAdapter:RestaurantAdapter){
        if(loadRestaurantsGeo){
            val userLocation = getCenterOfStockholm()
            val radiusKm = 20.0
            firestoreViewModel.getRestaurantsGeo(userLocation,radiusKm,restaurantAdapter)
        }
    }

    fun loadRestaurantMenu(restaurantId:String,restaurantMenuAdapter: RestaurantMenuAdapter){
        firestoreViewModel.getMenuItems(restaurantId,restaurantMenuAdapter)

    }

    private fun loadRestaurantsByDefault(restaurantAdapter:RestaurantAdapter){
        //val userLocation = getCenterOfStockholm()
        //val radiusKm = 20.0
        //firestoreViewModel.getRestaurantsGeo(userLocation,radiusKm,restaurantAdapter)
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
