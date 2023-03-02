package com.example.foodhero.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhero.R
import com.example.foodhero.fragment.HomeFragment
import com.example.foodhero.global.downloadImageFromStorage
import com.example.foodhero.struct.MenuItem


class RestaurantMenuAdapter(
    private val fragment: HomeFragment):
    RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolder>() {

    private val menuList = ArrayList<MenuItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item_card, parent, false)
        return ViewHolder(view)
    }

    fun clearView(){
        if(menuList.isNotEmpty()){
            val lastIndex = itemCount
            menuList.clear()
            notifyItemRangeRemoved(0,lastIndex)
        }
    }

    fun addMenuItems(items:ArrayList<MenuItem>){
        val start = itemCount
        menuList.addAll(items)
        notifyItemRangeInserted(start,items.size)
    }

    fun addMenuItem(item: MenuItem){
        menuList.add(item)
        notifyItemInserted(itemCount)
    }

    fun getMenuItem(pos:Int): MenuItem?{
        if(pos>=itemCount){return null}
        return menuList[pos]
    }

    fun putItemInBasket(pos:Int){
        val menuItem = getMenuItem(pos)
        menuItem?:return
        fragment.putSelectedFoodInCart(menuItem)
    }

    fun showUserSomeLove(pos:Int){
        val menuItem = getMenuItem(pos)
        menuItem?:return
        fragment.showSomeLoveBack(menuItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(menuList.isEmpty()){return}
        var userEmail = ""
        val itemsViewModel = menuList[position]
        fragment.downloadImageFromStorage(fragment.getRestaurantMenuItemLoggoRef(itemsViewModel.loggoDownloadUrl),holder.loggoImageView)
        if(fragment.getCurrentUserEmail().also{userEmail = it } != ""){
            fragment.checkIfUserLikesIt(userEmail,menuList[position].menuItemId!!).get().addOnCompleteListener {
                if(it.isSuccessful && it.result.exists()){
                    holder.imageYouHeartMeButton.alpha = 1.0f
                }
            }
        }
        else{
            holder.imageYouHeartMeButton.isEnabled = false
        }
        holder.nameTextView.text = itemsViewModel.name
        holder.ingredientsTextView.text = itemsViewModel.getIngredients()
        holder.priceTextView.text = itemsViewModel.getCurrentPrice()

        //holder.imageYouHeartMeButton.alpha = if(menuList[position].userComments?.contains(userEmail) == true)1.0f else 0.5f

    }

    override fun getItemCount(): Int {
        return menuList.size
    }
    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageOrderButton: ImageButton = itemView.findViewById(R.id.imageOrderButton)
        val imageYouHeartMeButton: ImageButton = itemView.findViewById(R.id.imageLikeButton)
        val loggoImageView: ImageView = itemView.findViewById(R.id.menuItemLoggo)
        val nameTextView: TextView = itemView.findViewById(R.id.menuItemName)
        val ingredientsTextView: TextView = itemView.findViewById(R.id.menuItemIngredients)
        val priceTextView: TextView = itemView.findViewById(R.id.menuItemPrice)

        init{
            imageOrderButton.setOnClickListener{
                putItemInBasket(bindingAdapterPosition)
            }
            imageYouHeartMeButton.setOnClickListener{
                if(imageYouHeartMeButton.alpha != 1.0f){
                    showUserSomeLove(bindingAdapterPosition)
                    imageYouHeartMeButton.alpha = 1.0f
                }
            }
        }
    }
}