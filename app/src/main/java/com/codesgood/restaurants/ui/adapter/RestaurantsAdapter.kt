package com.codesgood.restaurants.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codesgood.restaurants.R
import com.codesgood.restaurants.data.model.Restaurant

/**
 * @author Amilcar Serrano
 */
class RestaurantsAdapter(

    //Restaurants already fetched from the endpoint
    private val restaurants: ArrayList<Restaurant>,

    //Total amount of restaurants in the area (the ones to be downloaded till final scroll)
    private var totalRestaurantsInArea: Int,

    //Listener that asks for more restaurants
    private val listener: RestaurantListListener
) :
    RecyclerView.Adapter<RestaurantsAdapter.RestaurantViewHolder>() {

    //Interfaces that opens a window in order to request more restaurants
    interface RestaurantListListener {
        fun onNewDataRequired(offset: Int)
    }

    //Updates the list of restaurants to be shown
    fun updateRestaurants(newRestaurants: ArrayList<Restaurant>, totalInArea: Int) {
        totalRestaurantsInArea = totalInArea

        restaurants.clear()
        restaurants.addAll(newRestaurants)

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
        //If the restaurantName view is null, it means this holder contains the loading view
        //that means we don't need to bind it, just notify we need to request more restaurants.
        if (holder.restaurantName != null) {
            val restaurant = restaurants[position]
            holder.restaurantName.text = restaurant.name
            holder.restaurantAddress?.text = restaurant.address

            //Paints odds an pairs in different colors
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, if (position % 2 == 0) R.color.white else R.color.light_grey))
        } else {
            listener.onNewDataRequired(restaurants.size)
        }
    }

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView? = itemView.findViewById(R.id.restaurantName)
        val restaurantAddress: TextView? = itemView.findViewById(R.id.restaurantAddress)
    }

    companion object {
        const val RESTAURANT_VIEW_TYPE = 1
        const val LOADING_VIEW_TYPE = 0
    }
}