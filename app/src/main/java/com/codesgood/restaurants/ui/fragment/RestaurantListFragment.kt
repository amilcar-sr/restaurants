package com.codesgood.restaurants.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.codesgood.restaurants.R
import com.codesgood.restaurants.ui.adapter.RestaurantsAdapter
import com.codesgood.restaurants.ui.viewmodel.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_restaurant_list.*

class RestaurantListFragment : Fragment(), RestaurantsAdapter.RestaurantListListener {

    private lateinit var restaurantAdapter: RestaurantsAdapter
    private lateinit var model: MainViewModel
    private lateinit var disposableObserver: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = activity?.run { ViewModelProviders.of(this).get(MainViewModel::class.java) } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurantAdapter = RestaurantsAdapter(ArrayList(), model.restaurantsInArea, this)

        restaurants_recycler.layoutManager = LinearLayoutManager(context)
        restaurants_recycler.adapter = restaurantAdapter

        val restaurants = model.restaurants.value
        if (restaurants != null && restaurants.size > 0 && restaurantAdapter.itemCount == 0) {
            restaurantAdapter.updateRestaurants(restaurants, model.restaurantsInArea)
        }

        model.restaurants.observe(this, Observer {
            no_items_text.visibility = if (it.size > 0) View.GONE else View.VISIBLE
            restaurantAdapter.updateRestaurants(it, model.restaurantsInArea)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::disposableObserver.isInitialized) {
            disposableObserver.dispose()
        }
    }

    override fun onNewDataRequired(offset: Int) {
        disposableObserver = model.fetchRestaurants(offset).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                model.onNewRestaurantsLoaded(it.data, it.total)
            }
    }

    companion object {
        const val TAG = "RestaurantListFragment"
    }
}