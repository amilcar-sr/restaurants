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

class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel

    companion object {
        const val LOCATION_PERMISSION_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_fragment_container, SplashFragment(), SplashFragment.TAG)
                .commit()
        }
    }

    @SuppressLint("MissingPermission")
    override fun listenForTokenAndLocation() {
        if (verifyLocationPermission()) {
            viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

            disposableObserver = tokenAndLocationObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { error -> Log.e("TokenOrLocationError", error.message) }
                .flatMap {
                    NetworkService.generateAPI(it.token)
                    viewModel.onLocationFetched(it.location)
                    viewModel.fetchRestaurants(0).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                }.subscribe {
                    showMainFragment()
                    viewModel.onNewRestaurantsLoaded(it.data, it.total)
                }
        }
    }

    private fun showMainFragment() {
        val mainFragment = MainFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, mainFragment, MainFragment.TAG)
            .commit()
    }
}