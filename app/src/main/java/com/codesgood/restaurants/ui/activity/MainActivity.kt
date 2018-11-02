package com.codesgood.restaurants.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.codesgood.restaurants.R
import com.codesgood.restaurants.data.network.NetworkService
import com.codesgood.restaurants.ui.fragment.MainFragment
import com.codesgood.restaurants.ui.fragment.SplashFragment
import com.codesgood.restaurants.ui.viewmodel.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author Amilcar Serrano
 */
class MainActivity : BaseActivity() {

    //ViewModel that will held the selected location and list of restaurant's nearby
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Showing the splash fragment on first load
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, SplashFragment(), SplashFragment.TAG)
                .commit()
        }
    }

    @SuppressLint("MissingPermission")
    override fun listenForTokenAndLocation() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        disposableObserver = tokenAndLocationObservable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { error -> Log.e("TokenOrLocationError", error.message) }
            .flatMap {
                //Regenerating API with auth header
                NetworkService.generateAPI(it.token)

                //Requesting the first batch of restaurants from the endpoint
                //using the user's location (dummy in case the user didn't provide location permissions)
                viewModel.onLocationFetched(it.location)
                viewModel.fetchRestaurants(0).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
            }.subscribe {
                showMainFragment()

                //Notifying the ViewModel about new restaurants fetched
                viewModel.onNewRestaurantsLoaded(it.data, it.total)
            }
    }

    private fun showMainFragment() {
        val mainFragment = MainFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.fragmentContainer, mainFragment, MainFragment.TAG)
            .commit()
    }
}