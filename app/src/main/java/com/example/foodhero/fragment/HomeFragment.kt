package com.example.foodhero.fragment
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
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


class HomeFragment(intent: Intent) : BaseFragment() {
    private lateinit var recyclerViewRestaurant: RecyclerView
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var restaurantMenuAdapter: RestaurantMenuAdapter
    private lateinit var menuItemSearch: AppCompatEditText
    private var lastShownId:String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetDialog(R.layout.bottom_sheet_restaurant)
        setBottomSheetSearchDialog(R.layout.bottom_sheet_search)
        //setSearchKeyboard()
        setRefreshButton()
        setRecyclerView()
        setEventListener(view)
        setBottomSheetEvent()
        loadRestaurants()
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


    private fun setEventListener(view:View){
        val topSearchMenu = getHomeBinding().userPositionLayout
        topSearchMenu.setOnClickListener {
            bottomSheetSearchDialog.show()

        }
        /*bottomSheetSearchDialog.findViewById<LinearLayout>(R.id.searchDialogLayout).setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_UP -> {
                    bottomSheetSearchDialog.dismiss()
                }
            }
            v.performClick()
            true
        }

        nestedScrollView.setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_UP -> {
                    closeSearchKeyboard()
                }
            }
            nestedScrollView.performClick()
            true
        }

        menuItemSearch.setOnEditorActionListener { _, keyCode, event ->
            if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN) || keyCode == EditorInfo.IME_ACTION_SEARCH) {
                closeSearchKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }*/

    }

    private fun setBottomSheetEvent(){
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
           // R.id.navigateProfile->moveToActivityAndPutOnTop(Intent(this, OrderActivity::class.java))

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

    /*
    *   ##########################################################################
    *               LOAD RESTAURANTS
    *   ##########################################################################
    */

    private fun refreshRestaurants(){
        clearCathegorysContainer()
        clearRestaurantAdapter()
        getMainActivity().loadRestaurants(restaurantAdapter)
    }

    private fun loadRestaurants(){
        refreshRestaurants()
    }

    private fun sameRestaurantAsBefore(newRestaurantId:String):Boolean{
        return newRestaurantId == lastShownId
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
        lastShownId = restaurant.restaurantId

        clearRestaurantMenuAdapter()

        getMainActivity().downloadImageFromStorage(
            getMainActivity().getRestaurantLoggoRef(restaurant.loggoDownloadUrl),
            bottomSheetDialog.findViewById<AppCompatImageView>(R.id.restImage))

        bottomSheetDialog.findViewById<TextView>(R.id.restName).text = restaurant.name
        bottomSheetDialog.findViewById<TextView>(R.id.restInfo).text = restaurant.getRestaurantInfo()
        bottomSheetDialog.findViewById<TextView>(R.id.restRating).text = restaurant.getUserRatingString()
        bottomSheetDialog.findViewById<TextView>(R.id.restHours).text = restaurant.getOpeningHours()
        bottomSheetDialog.findViewById<TextView>(R.id.restDeliveryTime).text = restaurant.getDeliveryTime()

        bottomSheetDialog.show()
        getMainActivity().loadRestaurantMenu(restaurant.restaurantId,restaurantMenuAdapter)

    }


    /*
    *   ##########################################################################
    *               ON RESUME ON PAUSE ON STOP
    *   ##########################################################################
    */

    /*override fun onResume(){
        super.onResume()
    }

    override fun onPause(){
        super.onPause()
    }

    override fun onStop(){
        super.onStop()
    }*/
}