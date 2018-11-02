package com.codesgood.restaurants.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codesgood.restaurants.R
import com.codesgood.restaurants.data.model.Restaurant
import com.codesgood.restaurants.ui.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Responsible of showing the map and the restaurants locations
 *
 * @author Amilcar Serrano
 */
class RestaurantMapFragment : Fragment(), OnMapReadyCallback {

    //Markers placed in map
    private var markers = ArrayList<Marker>()

    //Model that will be serving the restaurants and selected/user location
    private lateinit var model: MainViewModel

    //Disposable that will be destroyed when needed
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run { ViewModelProviders.of(this).get(MainViewModel::class.java) } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        //Updating map camera to point to selected/user location
        val userLocation = model.location
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 12f)
        map?.animateCamera(cameraUpdate)

        map?.setOnMapLongClickListener { it ->

            //Updating ViewModel's location
            model.onNewLocationSelected(it)

            //Disposing of observable if needed
            if (::disposable.isInitialized) {
                disposable.dispose()
            }

            //Requesting new list of restaurants
            disposable = model.fetchRestaurants(0).subscribeOn(Schedulers.newThread()).subscribe {
                model.onNewRestaurantsLoaded(it.data, it.total)
            }
        }

        //Adding restaurants markers to map
        addMarkersToMap(model.restaurants.value ?: ArrayList(), map)

        //Listening to restaurants objects
        model.restaurants.observe(this, Observer {
            markers.forEach(Marker::remove)
            markers.clear()
            addMarkersToMap(it, map)
        })
    }

    private fun addMarkersToMap(restaurants: ArrayList<Restaurant>, map: GoogleMap?) {
        restaurants.forEach {
            val stringCoordinates = it.coordinates.split(",")
            val restaurantLocation = LatLng(stringCoordinates[0].toDouble(), stringCoordinates[1].toDouble())

            addMarker(it.name, restaurantLocation, BitmapDescriptorFactory.HUE_RED, map)
        }

        //Adding selected/user location to map and showing its info window
        val marker = addMarker(getString(R.string.selected_location), model.location, BitmapDescriptorFactory.HUE_CYAN, map)
        marker?.showInfoWindow()
    }

    private fun addMarker(name: String, position: LatLng, color: Float, map: GoogleMap?): Marker? {
        val marker = map?.addMarker(MarkerOptions().position(position).title(name).icon(BitmapDescriptorFactory.defaultMarker(color)))

        if (marker != null) {
            markers.add(marker)
        }

        return marker
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::disposable.isInitialized) {
            disposable.dispose()
        }
    }

    companion object {
        const val TAG = "RestaurantMapFragment"
    }
}