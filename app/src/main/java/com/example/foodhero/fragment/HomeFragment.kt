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
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch


class HomeFragment(intent: Intent) : BaseFragment() {
    private lateinit var recyclerViewRestaurant: RecyclerView
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var restaurantMenuAdapter: RestaurantMenuAdapter
    private val totalCathegoryCounter = CathegoryCounter()
    private val listOfKeywords = ArrayList<String>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logMessage("on create home")
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

    /*
    *   ##########################################################################
    *               RECYCLER VIEW
    *   ##########################################################################
    */
    private fun setRecyclerView(){
        recyclerViewRestaurant = getHomeBinding().restaurantRecyclerview
        restaurantAdapter = RestaurantAdapter(this)
        recyclerViewRestaurant.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewRestaurant.adapter = restaurantAdapter
    }

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
    }

    private fun setBottomSheetRestaurantEvent(){
        val closeBtn = bottomSheetDialog.findViewById<AppCompatImageButton>(R.id.closeMenuBtn)
        val showInfoBtn = bottomSheetDialog.findViewById<AppCompatImageButton>(R.id.showInfoBtn)
        recyclerViewMenu = bottomSheetDialog.findViewById<RecyclerView>(R.id.menuItemsRecyclerview)

        restaurantMenuAdapter = RestaurantMenuAdapter(this)
        recyclerViewMenu.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewMenu.adapter = restaurantMenuAdapter

        closeBtn.setOnClickListener{
            bottomSheetDialog.dismiss()
        }
        showInfoBtn.setOnClickListener{
            //bottomSheetDialog.dismiss()
            getMainActivity().navigateOnlyIfUserIsAllowed(ActivityInstance.ACTIVITY_ORDER)
            //getMainActivity().moveToActivityAndPutOnTop(Intent(getMainActivity(),OrderActivity::class.java))
        }
    }

   @SuppressLint("ClickableViewAccessibility")
   private fun setBottomSheetSearchEvent(){
        val goBackBtn = bottomSheetDialog.findViewById<AppCompatImageButton>(R.id.goBackBtn)
        val searchField = bottomSheetDialog.findViewById<AppCompatEditText>(R.id.menuItemSearch)
        val searchLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.searchDialogLayout)
       /*val searchContainer = bottomSheetDialog.findViewById<LinearLayout>(R.id.searchDialogContainerLayout)
       for(keyWord in listOfKeywords){
           val searchItem = SearchItem(keyWord,::searchForRestaurantByKeyWord,requireContext(),null)
           searchContainer.addView(searchItem,searchContainer.childCount)
       }*/


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
                //searchField.hideKeyboard()
                //searchForRestaurantByKeyWord(searchField.text.toString().capitalizeSentence())
                val suggestionList = ArrayList<String>(5)
                if(checkForSuggestion(
                    searchField.text.toString().capitalizeSentence(),
                    suggestionList,
                    listOfKeywords)){
                    logMessage("tested")
                }
                else{
                    logMessage("not tested")
                }
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
        pickLocation.setOnClickListener {
            bottomSheetDialog.dismiss()
            openBottomSheetPickCity()
        }
        pickGps.setOnClickListener {
            if(pickGpsImg.visibility == GONE){
                bottomSheetDialog.dismiss()
                if(getMainActivity().locationPermissionIsProvided()){
                    pickGpsImg.visibility = VISIBLE
                    getMainActivity().setCityOfChoice(getString(R.string.user_allowed_geo))
                    setUserLocationText()
                    refreshRestaurants()
                }
                else{
                    updateMessageDialog("Aktivera platsinfo i inställningar")
                    showMessage()
                }

            }
       }

    }

    private fun setBottomSheetPickCity(){
        val citySelected = getMainActivity().getCityOfChoice()
        val cityContainer = bottomSheetDialog.findViewById<LinearLayout>(R.id.cityContainerLayout)
        val cityClose = bottomSheetDialog.findViewById<AppCompatImageView>(R.id.cityCloseDialog)

        fun saveNewCity(city:String){
            for(cityItem in cityContainer.children){
                if(cityItem is CityItem && cityItem.city!=city){
                    cityItem.unCheckMe()
                }
            }
            getMainActivity().setCityOfChoice(city)
            setUserLocationText()
            bottomSheetDialog.dismiss()
            refreshRestaurants()
        }

        cityClose.setOnClickListener {
            bottomSheetDialog.dismiss()
            //openBottomSheetPosition()
        }

        val listOfCitys = getMainActivity().getCitiesWhereFoodHeroExist()
        listOfCitys.cities?:return
        for(city:String in listOfCitys.cities!!){
            val selected = city == citySelected
            val c = CityItem(city,selected,::saveNewCity,getMainActivity(),null)
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

    fun getMainActivity():MainActivity{
        return requireActivity() as MainActivity
    }

    /*
    *   ##########################################################################
    *               LOAD RESTAURANTS
    *   ##########################################################################
    */

    private fun clearRestaurantAdapter(){
        restaurantAdapter.clearView()
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
        loadRestaurants()
    }

    private fun searchForRestaurantByKeyWord(keyWord:String){
        if(keyWord.isNotEmpty() && listOfKeywords.contains(keyWord)){
            bottomSheetDialog.dismiss()
            getMainActivity().loadRestaurantsByKeyWord(totalCathegoryCounter.listOfIds,keyWord,restaurantAdapter)
        }
    }

    private fun loadRestaurants(){
        getMainActivity().loadRestaurants(restaurantAdapter)
    }

    private fun sameRestaurantAsBefore(newRestaurantId:String):Boolean{
        return isBottomSheetInitialized() &&
                (newRestaurantId == bottomSheetDialog.lastId) &&
                bottomSheetDialog.dialogInstance == DialogInstance.BOTTOM_SHEET_RESTAURANT
    }

    fun addCathegorysToView(listOfCat:MutableMap<String,CathegoryCounter>){
        viewLifecycleOwner.lifecycleScope.launch{
            val catContainer = getHomeBinding().restaurantCatContainerLayout
            totalCathegoryCounter.resetValues()
            for(lbl in listOfCat.keys){
                val catCounter = listOfCat[lbl]
                catCounter?:continue
                totalCathegoryCounter.addIdList(catCounter.listOfIds)
                val cat = CathegoryItem(lbl,catCounter,::sortRestaurantsByCat,requireContext(),null)
                catContainer.addView(cat,catContainer.childCount)
            }
            val cat = CathegoryItem("Visa Alla",totalCathegoryCounter,::sortRestaurantsByCat,requireContext(),null)
            catContainer.addView(cat,catContainer.childCount)
            collectKeyWords()
        }
    }

    private fun collectKeyWords(){
        listOfKeywords.clear()
        for(restaurant in restaurantAdapter.restaurantList){
            restaurant.keyWords?:continue
            for(keyword in restaurant.keyWords){
                if(!listOfKeywords.contains(keyword)){
                    listOfKeywords.add(keyword)
                }
            }
        }
    }

    private fun sortRestaurantsByCat(ids:List<String>){
        clearRestaurantAdapter()
        getMainActivity().loadRestaurantsByCathegory(ids,restaurantAdapter)
    }

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

    fun putSelectedFoodInCart(menuItem:com.example.foodhero.struct.MenuItem){
        getMainActivity().putFoodItemIntoCart(menuItem)
    }


    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */

    /*override fun onResume(){
        logMessage("on resume fragment resume")
        super.onResume()
    }

    override fun onPause(){
        logMessage("on paus fragment pause")
        super.onPause()
    }

    override fun onStop(){
        logMessage("on stop fragment home")
        super.onStop()
    }

    override fun onDestroy(){
        logMessage("on destroy fragment home")
        super.onStop()
    }*/
}