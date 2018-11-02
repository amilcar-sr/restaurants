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

/**
 * Responsible of showing the list of restaurants and ask for more of them if needed
 *
 * @author Amilcar Serrano
 */
class RestaurantListFragment : Fragment(), RestaurantsAdapter.RestaurantListListener {

    //Adapter that binds the restaurants items into the RecyclerView
    private lateinit var restaurantAdapter: RestaurantsAdapter

    //ViewModel that provides the list of restaurants and updates regarding them
    private lateinit var model: MainViewModel

    //Disposable observer tha is destroyed when needed
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

        restaurantsRecycler.layoutManager = LinearLayoutManager(context)
        restaurantsRecycler.adapter = restaurantAdapter

        //Adding restaurants to the list if needed
        val restaurants = model.restaurants.value
        if (restaurants != null && restaurants.size > 0 && restaurantAdapter.itemCount == 0) {
            restaurantAdapter.updateRestaurants(restaurants, model.restaurantsInArea)
        }

        //Listening to restaurants updates
        model.restaurants.observe(this, Observer {
            noItemsText.visibility = if (it.size > 0) View.GONE else View.VISIBLE
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