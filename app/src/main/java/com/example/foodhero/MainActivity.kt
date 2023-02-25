package com.example.foodhero
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.foodhero.activity.*
import com.example.foodhero.adapter.RestaurantAdapter
import com.example.foodhero.adapter.RestaurantMenuAdapter
import com.example.foodhero.database.AuthRepo
import com.example.foodhero.database.FirestoreViewModel
import com.example.foodhero.databinding.ActivityMainBinding
import com.example.foodhero.fragment.HomeFragment
import com.example.foodhero.global.*
import com.example.foodhero.struct.FoodHeroInfo
import com.example.foodhero.struct.PurchasedItem
import com.example.foodhero.widgets.MessageToUser
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var permissionsList: ArrayList<String>
    private lateinit var bottomNavMenu: BottomNavigationView
    private var foodHeroInfo = FoodHeroInfo()
    private var permissionsCount = 0
    private var informUserToSignIn:MessageToUser? = null
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val auth = AuthRepo()
    private var currentFragment:FragmentInstance? = null
    private val intentFilter = IntentFilter()
    private var permissionsStr = arrayOf<String>(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION,
        POST_NOTIFICATIONS
    )
    private var permissionsLauncher =
        registerForActivityResult<Array<String>, Map<String, Boolean>>(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String, Boolean>?> {
                val list: ArrayList<Boolean> = ArrayList(it.values)
                permissionsList = ArrayList()
                val missingPermission = ArrayList<String>()
                permissionsCount = 0
                for (i in 0 until list.size) {
                    if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                        permissionsList.add(permissionsStr[i])
                    } else if(!hasPermission(this, permissionsStr[i])) {
                        missingPermission.add(permissionsStr[i])
                        permissionsCount++
                    }
                }
                if (permissionsList.size > 0) {
                    askForPermissions(permissionsList)
                }else if (missingPermission.size > 0 &&
                            (missingPermission.contains(ACCESS_FINE_LOCATION) ||
                            missingPermission.contains(ACCESS_COARSE_LOCATION))) {
                    setCityOfChoice(getString(R.string.user_did_not_allow_geo))
                    launchScreenBasedOnSecurityLevel()
                }else{
                    shouldSetSharedPreference()
                    launchScreenBasedOnSecurityLevel()
                }
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!auth.isUserLoggedIn()){
            moveToActivityAndFinish(Intent(this,LoginActivity::class.java))
        }
        else{ launchPermissionRequest() }
    }

    private fun launchScreenBasedOnSecurityLevel(){
        auth.userCustomClaims().addOnSuccessListener(
            OnSuccessListener<GetTokenResult> { result ->
                val isAdmin: Any? = result.claims[CLAIMS_ROLE]
                if(isAdmin != null && isAdmin == ADMIN_USER){
                    moveToActivityAndFinish(Intent(this,RestAdminActivity::class.java))
                }
                else{ startMainApp() }
            })
    }

    private fun startMainApp(){
        setCloseAppCallback()
        setContentView(R.layout.activity_main)
        setMessageToUser()
        setViewModel()
        setDataBinding()
        setBottomNavigationMenu()
        setOnBackNavigation()
        loadListOfCitiesWhereFoodHeroExist()
        Toast.makeText(applicationContext, "Välkommen tillbaka ${auth.getEmail()}.", Toast.LENGTH_SHORT).show()
        navigateToFragment(FragmentInstance.FRAGMENT_MAIN_HOME)
    }

      /*
    *   ##########################################################################
    *               SET BINDING AND OTHER STUFF
    *   ##########################################################################
    */

    private fun setMessageToUser(){
        if(auth.userIsAnonymous()){
            informUserToSignIn = MessageToUser(this,null)
            informUserToSignIn!!.setPositiveCallback{
                navigateToSignUpActivity()
            }
            informUserToSignIn!!.setNeutralCallback{
                navigateToLogInActivity()
            }
        }
    }

    private fun setCloseAppCallback(){
        intentFilter.addAction(APP_ACTION_LOG_OUT)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                //logMessage("onReceive Logout in progress")
                moveToActivityAndFinish(Intent(context,LoginActivity::class.java))
            }
        }, intentFilter)
    }

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
                R.id.navigateFavorite->navigateOnlyIfUserIsAllowed(ActivityInstance.ACTIVITY_FAVORITE)
                R.id.navigateCart->navigateOnlyIfUserIsAllowed(ActivityInstance.ACTIVITY_ORDER)
                R.id.navigateProfile->navigateOnlyIfUserIsAllowed(ActivityInstance.ACTIVITY_PROFILE)
            }
            true
        }
    }

    private fun userNeedToSignUp():Boolean{
        if(auth.userIsAnonymous()){
            informUserToSignIn?.showLoginRequiredMessage()
            return true
        }
        return false
    }

    fun navigateOnlyIfUserIsAllowed(activityInstance:ActivityInstance){
        if(!userNeedToSignUp()){
            when(activityInstance){
                ActivityInstance.ACTIVITY_FAVORITE->moveToActivityAndReOrder(Intent(this,FavoriteActivity::class.java))
                ActivityInstance.ACTIVITY_ORDER->moveToActivityAndReOrder(Intent(this,OrderActivity::class.java))
                ActivityInstance.ACTIVITY_PROFILE->moveToActivityAndReOrder(Intent(this, ProfilActivity::class.java))
            }
        }
    }

    private fun navigateToSignUpActivity(){
        val intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("Fragment",FragmentInstance.FRAGMENT_SIGN_UP.toString())
        moveToActivityAndPutOnTop(intent)
    }

    private fun navigateToLogInActivity(){
        val intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("Fragment",FragmentInstance.FRAGMENT_LOGIN_USER.toString())
        moveToActivityAndPutOnTop(intent)
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
        /*else if(!permissionDialogIsOpen) {
            permissionDialogIsOpen = true
            showPermissionDialog()
        }*/
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
            FragmentInstance.FRAGMENT_MAIN_HOME->applyTransaction(HomeFragment())
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

    fun setCityOfChoice(city:String){
        writeStringToSharedPreference(userCityTag(),city)
    }

    private fun userCityTag():String{
        return auth.userUid() + getString(R.string.user_city)
    }

    fun getCheckMarkerVisibility():Int{
        return if(getCityOfChoice() == getString(R.string.user_allowed_geo))VISIBLE else GONE
    }

    fun getCityOfChoice():String{
        return retrieveStringFromSharedPreference(userCityTag(),"")?:""
    }

    private fun shouldSetSharedPreference(){
        if(isACorrectCity(getCityOfChoice()))return
        setCityOfChoice(getString(R.string.user_allowed_geo))
    }

    private fun isACorrectCity(city:String):Boolean{
        return  city.isNotEmpty() &&
                city != getString(R.string.user_allowed_geo) &&
                city != getString(R.string.user_did_not_allow_geo)
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
        val cityOfChoice = getCityOfChoice()
        if(cityOfChoice == getString(R.string.user_allowed_geo)){
            val userLocation = getCenterOfStockholm()
            val radiusKm = 20.0
            firestoreViewModel.getRestaurantsGeo(userLocation,radiusKm,restaurantAdapter)
        }
        else if(isACorrectCity(cityOfChoice)){
            loadRestaurantsByCity(cityOfChoice,restaurantAdapter)
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
    *              PUT FOOD INTO CART
    *   ##########################################################################
    */

    fun putFoodItemIntoCart(PurchasedItem: com.example.foodhero.struct.MenuItem){
        if(!userNeedToSignUp()){
            val id = UUID.randomUUID().toString()
            val foodName = PurchasedItem.name
            val price = PurchasedItem.price
            val purchasedItem = PurchasedItem(id,foodName,price)
            val user = auth.getEmail()
            val db = FirebaseFirestore.getInstance()
            db.collection("Users").document(user).collection("ShoppingCart").document(id)
                .set(purchasedItem)
            logMessage(user)



            //Först ska menuItem samlas i varukorgen
            //Plocka ut menuItem och lägg i någon lista i Favorite klassen när man kör denna funktion.
            //Denna funktionen körs när man klickar på köpknappen och man är inloggad.

        }

    }


    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */
    /*override fun onResume(){
        super.onResume()
        logMessage("on resume main")
        //navigateOnResume()

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
        logMessage("on destroy main")
        super.onDestroy()
    }*/
}
