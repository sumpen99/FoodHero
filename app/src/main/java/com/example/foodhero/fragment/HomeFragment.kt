package com.example.foodhero.fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.activity.OrderActivity
import com.example.foodhero.adapter.RestaurantAdapter
import com.example.foodhero.adapter.RestaurantMenuAdapter
import com.example.foodhero.databinding.FragmentHomeBinding
import com.example.foodhero.global.FragmentInstance
import com.example.foodhero.global.downloadImageFromStorage
import com.example.foodhero.global.moveToActivity
import com.example.foodhero.struct.Restaurant


class HomeFragment(intent: Intent) : BaseFragment() {
    private lateinit var recyclerViewRestaurant: RecyclerView
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var restaurantMenuAdapter: RestaurantMenuAdapter
    private var lastShownId:String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetDialog(R.layout.bottom_sheet_restaurant)
        setRecyclerView()
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
    *               BOTTOM SHEET
    *   ##########################################################################
    */

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
            parentActivity.moveToActivity(Intent(parentActivity,OrderActivity::class.java))
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

    private fun loadRestaurants(){
        getMainActivity().loadRestaurants(restaurantAdapter)
    }

    private fun sameRestaurantAsBefore(newRestauranId:String):Boolean{
        return newRestauranId == lastShownId
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
        restaurantMenuAdapter.clearView()

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