package com.example.foodhero.fragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.activity.OrderActivity
import com.example.foodhero.adapter.RestaurantAdapter
import com.example.foodhero.adapter.RestaurantMenuAdapter
import com.example.foodhero.databinding.FragmentHomeBinding
import com.example.foodhero.global.*
import com.example.foodhero.struct.CathegoryCounter
import com.example.foodhero.struct.Restaurant
import com.example.foodhero.widgets.CathegoryItem
import com.example.foodhero.widgets.CityItem


class HomeFragment(intent: Intent) : BaseFragment() {
    private lateinit var recyclerViewRestaurant: RecyclerView
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var restaurantMenuAdapter: RestaurantMenuAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRefreshButton()
        setRecyclerView()
        setEventListener(view)
        loadRestaurants()
        setUserLocationText()
   }

    /*
    *   ##########################################################################
    *               CURRENT LOCATION TO LOAD RESTAURANTS FROM
    *   ##########################################################################
    */

    private fun setUserLocationText(){
        val userLocText = getHomeBinding().menuItemSearch
        userLocText.hint = getMainActivity().getCityOfChoice()
    }

    private fun updateUserLocationText(pickGpsImg:AppCompatImageView){
        if(getMainActivity().locationPermissionIsProvided()){
            if(pickGpsImg.visibility == VISIBLE){
                pickGpsImg.visibility = GONE
                getMainActivity().setLocationOfChoice(false)
            }
            else{
                pickGpsImg.visibility = VISIBLE
                getMainActivity().setLocationOfChoice(true)
                getMainActivity().setCityOfChoice("")
                // TODO UPDATE LIST IF NEEDED
            }
            setUserLocationText()
        }
        else{
            updateMessageDialog("Aktivera platsinfo i inställningar")
            showMessage()
        }
    }

    /*
    *   ##########################################################################
    *               RECYCLER VIEW
    *   ##########################################################################
    */
    private fun setRecyclerView(){
        recyclerViewRestaurant = getHomeBinding().restaurantRecyclerview
        restaurantAdapter = RestaurantAdapter(getMainActivity(),this)
        recyclerViewRestaurant.layoutManager = LinearLayoutManager(activityContext)
        recyclerViewRestaurant.adapter = restaurantAdapter
    }

    /*
    *   ##########################################################################
    *               SET REFRESH BUTTON
    *   ##########################################################################
    */

    private fun setRefreshButton(){
        val refresh = getHomeBinding().refreshButton
        refresh.setOnClickListener{
            refreshRestaurants()
        }
    }

    /*
    *   ##########################################################################
    *               SET SEARCH KEYBOARD AND CLOSE
    *   ##########################################################################
    */

    /*private fun setSearchKeyboard(){
        menuItemSearch = getHomeBinding().menuItemSearch
    }

    private fun closeSearchKeyboard(){
        menuItemSearch.hideKeyboard()
    }

    private fun keyBoardIsFocused():Boolean{
        if(menuItemSearch.isFocused){
            closeSearchKeyboard()
            return true
        }
        return false
    }*/

    /*
    *   ##########################################################################
    *               BOTTOM SHEET AND EVENTLISTENER
    *   ##########################################################################
    */

    private fun openBottomSheetSearch(){
        if(!dismissOpenBottomSheetDialog()){
            if(!dismissNewBottomSheetDialogLayout(DialogInstance.BOTTOM_SHEET_SEARCH)){
                setBottomSheetDialog(R.layout.bottom_sheet_search,MATCH_PARENT,DialogInstance.BOTTOM_SHEET_SEARCH)
                setBottomSheetSearchEvent()
            }
            bottomSheetDialog.show()
        }
    }

    private fun openBottomSheetPosition(){
        if(!dismissOpenBottomSheetDialog()){
            if(!dismissNewBottomSheetDialogLayout(DialogInstance.BOTTOM_SHEET_PICK_LOCATION)){
                setBottomSheetDialog(R.layout.bottom_sheet_position,WRAP_CONTENT,DialogInstance.BOTTOM_SHEET_PICK_LOCATION)
                setBottomSheetPickLocationEvent()
            }
            bottomSheetDialog.show()
        }
    }

    private fun openBottomSheetPickCity(){
        if(!dismissOpenBottomSheetDialog()){
            if(!dismissNewBottomSheetDialogLayout(DialogInstance.BOTTOM_SHEET_PICK_CITY)){
                setBottomSheetDialog(R.layout.bottom_sheet_pick_city,MATCH_PARENT,DialogInstance.BOTTOM_SHEET_PICK_CITY)
                setBottomSheetPickCity()
            }
            bottomSheetDialog.show()
        }
    }

    private fun setEventListener(view:View){
        val topSearchMenu = getHomeBinding().openSearchWindow
        val pickLocationBtn = getHomeBinding().openPickLocationWindow
        topSearchMenu.setOnClickListener {
            openBottomSheetSearch()
        }
        pickLocationBtn.setOnClickListener {
            openBottomSheetPosition()
        }

        /*menuItemSearch.setOnEditorActionListener { _, keyCode, event ->
            if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN) || keyCode == EditorInfo.IME_ACTION_SEARCH) {
                closeSearchKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }*/

    }

    private fun setBottomSheetRestaurantEvent(){
        val closeBtn = bottomSheetDialog.findViewById<AppCompatImageButton>(R.id.closeMenuBtn)
        val showInfoBtn = bottomSheetDialog.findViewById<AppCompatImageButton>(R.id.showInfoBtn)
        recyclerViewMenu = bottomSheetDialog.findViewById<RecyclerView>(R.id.menuItemsRecyclerview)

        restaurantMenuAdapter = RestaurantMenuAdapter(getMainActivity(),this)
        recyclerViewMenu.layoutManager = LinearLayoutManager(activityContext)
        recyclerViewMenu.adapter = restaurantMenuAdapter

        closeBtn.setOnClickListener{
            bottomSheetDialog.dismiss()
        }
        showInfoBtn.setOnClickListener{
            bottomSheetDialog.dismiss()
            parentActivity.moveToActivityAndPutOnTop(Intent(parentActivity,OrderActivity::class.java))
        }
    }

   @SuppressLint("ClickableViewAccessibility")
   private fun setBottomSheetSearchEvent(){
        val goBackBtn = bottomSheetDialog.findViewById<AppCompatImageButton>(R.id.goBackBtn)
        val searchField = bottomSheetDialog.findViewById<AppCompatEditText>(R.id.menuItemSearch)
        val searchLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.searchDialogLayout)

        goBackBtn.setOnClickListener {
            searchField.text?.clear()
            bottomSheetDialog.dismiss()
        }

        searchLayout.setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_UP -> {
                    searchField.hideKeyboard()
                }
            }
            v.performClick()
            true
        }

        searchField.setOnEditorActionListener { _, keyCode, event ->
            if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN) || keyCode == EditorInfo.IME_ACTION_SEARCH) {
                searchField.hideKeyboard()
                //closeSearchKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBottomSheetPickLocationEvent(){
        val pickGps = bottomSheetDialog.findViewById<LinearLayout>(R.id.pickGpsLayout)
        val pickLocation = bottomSheetDialog.findViewById<LinearLayout>(R.id.pickLocationLayout)
        val pickGpsImg = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.gpsEnabledImageView)
        pickGpsImg.visibility = getMainActivity().getCheckMarkerVisibility()
        pickLocation.clickEffect()
        pickGps.clickEffect()
        pickLocation.setOnClickListener {
            bottomSheetDialog.dismiss()
            openBottomSheetPickCity()
        }
        pickGps.setOnClickListener {
            updateUserLocationText(pickGpsImg)
       }

    }

    private fun setBottomSheetPickCity(){
        val citySelected = getMainActivity().getCityOfChoice()
        val cityContainer = bottomSheetDialog.findViewById<LinearLayout>(R.id.cityContainerLayout)
        val cityClose = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.cityCloseDialog)
        val citySave = bottomSheetDialog.findViewById<LinearLayout>(R.id.layoutBottomCountry)

        fun uncheckItems(city:String){
            for(cityItem in cityContainer.children){
                if(cityItem is CityItem && cityItem.city!=city){
                    cityItem.unCheckMe()
                }
            }
        }

        fun getCityToSave():String{
            for(cityItem in cityContainer.children){
                if(cityItem is CityItem && cityItem.isActive){
                    return cityItem.city
                }
            }
            return ""
        }


        citySave.clickEffect()
        cityClose.clickEffect()
        cityClose.setOnClickListener {
            bottomSheetDialog.dismiss()
            openBottomSheetPosition()
        }
        citySave.setOnClickListener{
            val cityToSave = getCityToSave()
            if(cityToSave != "" && cityToSave != citySelected){
                getMainActivity().setCityOfChoice(cityToSave)
                setUserLocationText()
                bottomSheetDialog.dismiss()
                // TODO RELOAD RESTAURANTS
            }
            else{
                updateMessageDialog("Inget att spara")
            }
        }

        val listOfCitys = getMainActivity().getCitiesWhereFoodHeroExist()
        listOfCitys.cities?:return
        for(city:String in listOfCitys.cities!!){
            val selected = city == citySelected
            val c = CityItem(city,selected,::uncheckItems,parentActivity,null)
            cityContainer.addView(c,cityContainer.childCount)
        }
    }

    /*
    *   ##########################################################################
    *               IFRAGMENT FUNCTIONS
    *   ##########################################################################
    */

    override fun setFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun setFragmentView() {
        baseView = binding.root
    }

    override fun getFragmentID(): FragmentInstance {
        return  FragmentInstance.FRAGMENT_MAIN_HOME
    }

    private fun getHomeBinding():FragmentHomeBinding{
        return binding as FragmentHomeBinding
    }

    private fun getMainActivity():MainActivity{
        return activity as MainActivity
    }

    /*
    *   ##########################################################################
    *               LOAD RESTAURANTS
    *   ##########################################################################
    */

    private fun clearRestaurantAdapter(){
        restaurantAdapter.clearView()
        restaurantAdapter.clearCathegories()
    }

    private fun clearRestaurantMenuAdapter(){
        restaurantMenuAdapter.clearView()
    }

    private fun clearCathegorysContainer(){
        val catContainer = getHomeBinding().restaurantCatContainerLayout
        catContainer.removeAllViews()

    }

    private fun refreshRestaurants(){
        clearCathegorysContainer()
        clearRestaurantAdapter()
        getMainActivity().loadRestaurants(restaurantAdapter)
    }

    private fun loadRestaurants(){
        refreshRestaurants()
    }

    private fun sameRestaurantAsBefore(newRestaurantId:String):Boolean{
        return isBottomSheetInitialized() &&
                (newRestaurantId == bottomSheetDialog.lastId) &&
                bottomSheetDialog.dialogInstance == DialogInstance.BOTTOM_SHEET_RESTAURANT
    }

    fun addCathegorysToView(listOfCat:MutableMap<String,CathegoryCounter>){
        val catContainer = getHomeBinding().restaurantCatContainerLayout
        for(lbl in listOfCat.keys){
            val catCounter = listOfCat[lbl]
            catCounter?:continue
            val cat = CathegoryItem(lbl,catCounter,::sortRestaurantsByCat,parentActivity,null)
            //cat.setImageResource()
            catContainer.addView(cat,catContainer.childCount)
        }
    }

    /*
    *   ##########################################################################
    *               CALLBACK TO SORT RESTAURANT LIST
    *   ##########################################################################
    */

    private fun sortRestaurantsByCat(ids:List<String>){
        clearRestaurantAdapter()
        getMainActivity().loadRestaurantsByCathegory(ids,restaurantAdapter)
    }

    /*
    *   ##########################################################################
    *               SHOW RESTAURANTS
    *   ##########################################################################
    */

    fun showRestaurant(restaurant: Restaurant){
        if(sameRestaurantAsBefore(restaurant.restaurantId!!)){
            bottomSheetDialog.show()
            return
        }
        else if(dismissNewBottomSheetDialogLayout(DialogInstance.BOTTOM_SHEET_RESTAURANT)){
            clearRestaurantMenuAdapter()
        }
        else{
            setBottomSheetDialog(R.layout.bottom_sheet_restaurant, MATCH_PARENT,DialogInstance.BOTTOM_SHEET_RESTAURANT)
            setBottomSheetRestaurantEvent()
        }
        bottomSheetDialog.lastId = restaurant.restaurantId
        populateBottomSheetWithRestaurant(restaurant)
    }

    private fun populateBottomSheetWithRestaurant(restaurant:Restaurant){
        getMainActivity().downloadImageFromStorage(
            getMainActivity().getRestaurantLoggoRef(restaurant.loggoDownloadUrl),
            bottomSheetDialog.findViewById<AppCompatImageView>(R.id.restImage))

        bottomSheetDialog.findViewById<TextView>(R.id.restName).text = restaurant.name
        bottomSheetDialog.findViewById<TextView>(R.id.restInfo).text = restaurant.getRestaurantInfo()
        bottomSheetDialog.findViewById<TextView>(R.id.restRating).text = restaurant.getUserRatingString()
        bottomSheetDialog.findViewById<TextView>(R.id.restHours).text = restaurant.getOpeningHours()
        bottomSheetDialog.findViewById<TextView>(R.id.restDeliveryTime).text = restaurant.getDeliveryTime()

        bottomSheetDialog.show()
        getMainActivity().loadRestaurantMenu(restaurant.restaurantId!!,restaurantMenuAdapter)
    }


    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */

    /*override fun onResume(){
        logMessage("on resume")
        super.onResume()
    }

    override fun onPause(){
        logMessage("on paus")
        super.onPause()
    }

    override fun onStop(){
        logMessage("on stop")
        super.onStop()
    }*/
}