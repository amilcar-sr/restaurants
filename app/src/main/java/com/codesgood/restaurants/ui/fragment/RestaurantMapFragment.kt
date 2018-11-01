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


class RestaurantMapFragment : Fragment(), OnMapReadyCallback {

    private var markers = ArrayList<Marker>()
    private lateinit var model: MainViewModel
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        val userLocation = model.location
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 12f)
        map?.animateCamera(cameraUpdate)
        map?.setOnMapLongClickListener { it ->
            model.onNewLocationSelected(it)

            if (::disposable.isInitialized) {
                disposable.dispose()
            }

            disposable = model.fetchRestaurants(0).subscribeOn(Schedulers.newThread()).subscribe {
                model.onNewRestaurantsLoaded(it.data, it.total)
            }
        }

        addMarkersToMap(model.restaurants.value ?: ArrayList(), map)

        model.restaurants.observe(this, Observer {
            markers.forEach(Marker::remove)
            addMarkersToMap(it, map)
        })
    }

    private fun addMarkersToMap(restaurants: ArrayList<Restaurant>, map: GoogleMap?) {
        restaurants.forEach {
            val stringCoordinates = it.coordinates.split(",")
            val restaurantLocation = LatLng(stringCoordinates[0].toDouble(), stringCoordinates[1].toDouble())

            addMarker(it.name, restaurantLocation, BitmapDescriptorFactory.HUE_RED, map)
        }

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
        disposable.dispose()
    }

    companion object {
        const val TAG = "RestaurantMapFragment"
    }
}