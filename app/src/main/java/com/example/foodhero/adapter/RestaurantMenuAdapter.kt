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

    fun getMenu(pos:Int): MenuItem?{
        if(pos>=itemCount){return null}
        return menuList[pos]
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(menuList.isEmpty()){return}
        val itemsViewModel = menuList[position]
        fragment.getMainActivity().downloadImageFromStorage(fragment.getMainActivity().getRestaurantMenuItemLoggoRef(itemsViewModel.loggoDownloadUrl),holder.loggoImageView)
        holder.nameTextView.text = itemsViewModel.name
        holder.ingredientsTextView.text = itemsViewModel.getIngredients()
        holder.priceTextView.text = itemsViewModel.getCurrentPrice()
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val loggoImageView: ImageView = itemView.findViewById(R.id.menuItemLoggo)
        val nameTextView: TextView = itemView.findViewById(R.id.menuItemName)
        val ingredientsTextView: TextView = itemView.findViewById(R.id.menuItemIngredients)
        val priceTextView: TextView = itemView.findViewById(R.id.menuItemPrice)

        init{
            loggoImageView.setOnClickListener{
                //showRestaurantMenu(bindingAdapterPosition)
            }
        }
    }
}