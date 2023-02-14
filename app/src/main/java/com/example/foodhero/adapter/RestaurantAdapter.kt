package com.example.foodhero.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhero.MainActivity
import com.example.foodhero.R
import com.example.foodhero.fragment.HomeFragment
import com.example.foodhero.global.downloadImageFromStorage
import com.example.foodhero.struct.Restaurant

class RestaurantAdapter(private val activity:MainActivity,private val fragment: HomeFragment):RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    private val restaurantList = ArrayList<Restaurant>()
    var listOfCathegories = mutableMapOf <String,Int>()
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

    /*
    *   ##########################################################################
    *               CATEGORIES
    *   ##########################################################################
    */

    fun addNewCathegorie(name:String){
        if(!listOfCathegories.containsKey(name)){
            listOfCathegories[name] = 1
        }
        else{
            val cnt:Int? = listOfCathegories[name]
            cnt?:return
            listOfCathegories[name] = cnt+1
        }
    }

    fun loadAllCathegories(){
        if(listOfCathegories.isEmpty())return
        fragment.addCathegorysToView(listOfCathegories)
    }

    fun clearCathegories(){
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
        notifyItemInserted(itemCount)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(restaurantList.isEmpty()){return}
        val itemsViewModel = restaurantList[position]
        activity.downloadImageFromStorage(activity.getRestaurantLoggoRef(itemsViewModel.loggoDownloadUrl),holder.loggoImageView)
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
            loggoImageView.setOnClickListener{
                showRestaurantMenu(bindingAdapterPosition)
            }
        }
    }
}