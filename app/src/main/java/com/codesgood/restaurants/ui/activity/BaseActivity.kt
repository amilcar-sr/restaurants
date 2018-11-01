package com.codesgood.restaurants.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codesgood.restaurants.data.model.AuthLocation
import com.codesgood.restaurants.data.model.Token
import com.codesgood.restaurants.data.network.NetworkService
import com.codesgood.restaurants.data.network.PrivateConstants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

abstract class BaseActivity : AppCompatActivity() {

    abstract fun listenForTokenAndLocation()

    protected lateinit var disposableObserver: Disposable

    private lateinit var locationProvider: FusedLocationProviderClient

    private val tokenObservable =
        NetworkService.getAPI().getToken(PrivateConstants.CLIENT_ID, PrivateConstants.CLIENT_SECRET)

    @SuppressLint("MissingPermission")
    private val locationObservable: Observable<Location> = Observable.create<Location> { emitter ->
        locationProvider.lastLocation.addOnSuccessListener {
            emitter.onNext(it)
            emitter.onComplete()
        }

        locationProvider.lastLocation.addOnCanceledListener {
            emitter.onComplete()
        }

        locationProvider.lastLocation.addOnFailureListener {
            emitter.onError(it)
        }
    }

    protected val tokenAndLocationObservable: Observable<AuthLocation> = Observable.zip(
        tokenObservable,
        locationObservable,
        BiFunction<Token, Location, AuthLocation> { token: Token, location: Location ->
            return@BiFunction AuthLocation(
                token.accessToken,
                location
            )
        })

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MainActivity.LOCATION_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    listenForTokenAndLocation()
                } else {
                    //TODO: Show Dialog
                }
                return
            }
        }
    }

    protected fun verifyLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        if (savedInstanceState == null) {
            if (!verifyLocationPermission()) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    //TODO: Show dialog
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MainActivity.LOCATION_PERMISSION_REQUEST
                    )
                }
            } else {
                listenForTokenAndLocation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::disposableObserver.isInitialized) {
            disposableObserver.dispose()
        }
    }
}