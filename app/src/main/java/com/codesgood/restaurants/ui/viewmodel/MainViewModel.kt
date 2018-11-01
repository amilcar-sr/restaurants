package com.codesgood.restaurants.ui.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codesgood.restaurants.data.model.Restaurant
import com.codesgood.restaurants.data.model.RestaurantResponse
import com.codesgood.restaurants.data.network.NetworkConstants.COUNTRY_REQUEST
import com.codesgood.restaurants.data.network.NetworkConstants.MAX_REQUEST
import com.codesgood.restaurants.data.network.NetworkService
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable

class MainViewModel : ViewModel() {

    //Location to be used to fetch the restaurants
    lateinit var location: LatLng

    //Total of restaurants in the area fetched from the endpoint
    var restaurantsInArea: Int = 0

    //List of restaurants in the area
    val restaurants = MutableLiveData<ArrayList<Restaurant>>()

    /**
     * Called when a location has been fetched
     * @param userLocation User's location
     */
    fun onLocationFetched(userLocation: Location) {
        location = LatLng(userLocation.latitude, userLocation.longitude)
    }

    /**
     * Called when a new location has been selected
     * @param newLocation Location selected by user
     */
    fun onNewLocationSelected(newLocation: LatLng) {
        location = newLocation
        restaurants.value = ArrayList()
        restaurants.postValue(restaurants.value)
    }

    /**
     * Called when new restaurants have been fetched from the endpoint
     * @param newRestaurants List of restaurants fetched from the endpoint
     * @param totalInArea Total amount of restaurants in the area of the selected location
     */
    fun onNewRestaurantsLoaded(newRestaurants: ArrayList<Restaurant>, totalInArea: Int) {
        if (restaurants.value != null) {
            restaurants.value!!.addAll(newRestaurants)
        } else {
            restaurants.value = newRestaurants
        }

        restaurantsInArea = totalInArea
        restaurants.postValue(restaurants.value)
    }

    /**
     * Retrieves an Observable so observers are notified when new restaurants are fetched
     * @param offset Offset to be used in the restaurants request
     */
    fun fetchRestaurants(offset: Int): Observable<RestaurantResponse> {
        val locationString = StringBuilder().append(location.latitude).append(",").append(location.longitude).toString()
        return NetworkService.getAPI().fetchRestaurants(locationString, COUNTRY_REQUEST, MAX_REQUEST, offset)
    }
}