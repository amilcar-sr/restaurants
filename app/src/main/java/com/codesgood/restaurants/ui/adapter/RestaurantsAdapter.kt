package com.codesgood.restaurants.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codesgood.restaurants.R
import com.codesgood.restaurants.data.model.Restaurant
import com.codesgood.restaurants.data.network.NetworkConstants

class RestaurantsAdapter(
    private val restaurants: ArrayList<Restaurant>,
    private var totalRestaurantsInArea: Int,
    private val listener: RestaurantListListener
) :
    RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>() {

    interface RestaurantListListener {
        fun onNewDataRequired(offset: Int)
    }

    fun addRestaurants(newRestaurants: ArrayList<Restaurant>) {
        val currentSize = itemCount - 1
        val newSize = itemCount + newRestaurants.size - 2

        restaurants.addAll(newRestaurants)
        notifyItemRangeInserted(currentSize, newSize)
    }

    fun updateData(newRestaurants: ArrayList<Restaurant>, restaurantsInArea: Int) {
        restaurants.clear()
        restaurants.addAll(newRestaurants)
        totalRestaurantsInArea = restaurantsInArea
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return restaurants.size + if (restaurants.size < totalRestaurantsInArea) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        val finishedLoading = restaurants.size == totalRestaurantsInArea

        return if (finishedLoading) {
            RESTAURANT_VIEW_TYPE
        } else {
            if (position == restaurants.size) LOADING_VIEW_TYPE else RESTAURANT_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val layout = if (viewType == RESTAURANT_VIEW_TYPE) R.layout.item_restaurant else R.layout.item_loading
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        if (holder.restaurantName != null) {
            val restaurant = restaurants[position]
            holder.restaurantName.text = restaurant.name
            holder.restaurantAddress?.text = restaurant.address

            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, if (position % 2 == 0) R.color.white else R.color.light_grey))
        } else {
            listener.onNewDataRequired(restaurants.size / NetworkConstants.MAX_REQUEST)
        }
    }

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView? = itemView.findViewById(R.id.restaurant_name)
        val restaurantAddress: TextView? = itemView.findViewById(R.id.restaurant_address)
    }

    companion object {
        const val RESTAURANT_VIEW_TYPE = 1
        const val LOADING_VIEW_TYPE = 0
    }
}