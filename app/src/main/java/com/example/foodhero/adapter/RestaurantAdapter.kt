package com.example.foodhero.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhero.R
import com.example.foodhero.fragment.HomeFragment
import com.example.foodhero.global.downloadImageFromStorage
import com.example.foodhero.struct.CathegoryCounter
import com.example.foodhero.struct.Restaurant

class RestaurantAdapter(private val fragment: HomeFragment):RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    val restaurantList = ArrayList<Restaurant>()
    var listOfCathegories = mutableMapOf <String,CathegoryCounter>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_card, parent, false)
        return ViewHolder(view)
    }

    fun getRestaurant(pos:Int):Restaurant?{
        if(pos>=itemCount){return null}
        return restaurantList[pos]
    }

    fun showRestaurantMenu(pos:Int){
        val restaurant = getRestaurant(pos)
        restaurant?:return
        fragment.showRestaurant(restaurant)
    }

    fun containsKeyWord(){

    }

    /*
    *   ##########################################################################
    *               CATEGORIES
    *   ##########################################################################
    */

    fun addNewCathegorie(restaurant:Restaurant){
        restaurant.cathegoriesDishes?:return
        val cat = restaurant.cathegoriesDishes[0]
        val id = restaurant.restaurantId!!
        if(!listOfCathegories.containsKey(cat)){
            val catCnt = CathegoryCounter()
            catCnt.updateCounter(id)
            listOfCathegories[cat] = catCnt

        }
        else{
            listOfCathegories[cat]?.updateCounter(id)
        }
    }

    fun loadAllCathegories(){
        if(listOfCathegories.isEmpty())return
        fragment.addCathegorysToView(listOfCathegories)
        listOfCathegories.clear()
    }

    fun clearView(){
        if(restaurantList.isNotEmpty()){
            val lastIndex = itemCount
            restaurantList.clear()
            notifyItemRangeRemoved(0,lastIndex)
        }
    }

    fun addRestaurants(items:ArrayList<Restaurant>){
        val start = itemCount
        restaurantList.addAll(items)
        notifyItemRangeInserted(start,items.size)
    }

    fun addRestaurant(item: Restaurant){
        restaurantList.add(item)
        addNewCathegorie(item)
        notifyItemInserted(itemCount)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(restaurantList.isEmpty()){return}
        val itemsViewModel = restaurantList[position]
        fragment.downloadImageFromStorage(fragment.getRestaurantLoggoRef(itemsViewModel.loggoDownloadUrl),holder.loggoImageView)
        holder.nameTextView.text = itemsViewModel.name
        holder.categoryTextView.text = itemsViewModel.getCategoriesString()
        // holder.deliveryTypeImageView set image
        holder.deliveryCostTextView.text = itemsViewModel.getDeliveryCostString()
        holder.deliveryTimeTextView.text = itemsViewModel.getDeliveryTime()
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }
    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val loggoImageView: ImageView = itemView.findViewById(R.id.restaurantLoggoImage)
        val nameTextView: TextView = itemView.findViewById(R.id.restaurantNameText)
        val categoryTextView: TextView = itemView.findViewById(R.id.restaurantCategoryText)
        val deliveryCostTextView: TextView = itemView.findViewById(R.id.restaurantDeliveryCostText)
        val deliveryTimeTextView: TextView = itemView.findViewById(R.id.restaurantDeliveryTimeText)
        val deliveryTypeImageView: ImageView = itemView.findViewById(R.id.restaurantDeliveryTypeImage)

        init{
            ItemView.setOnClickListener{
                showRestaurantMenu(bindingAdapterPosition)
            }
        }
    }
}