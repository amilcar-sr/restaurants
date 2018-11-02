package com.codesgood.restaurants.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codesgood.restaurants.R
import com.codesgood.restaurants.data.model.AuthLocation
import com.codesgood.restaurants.data.model.Token
import com.codesgood.restaurants.data.network.NetworkService
import com.codesgood.restaurants.data.network.PrivateConstants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

/**
 * Takes care of requesting location permissions and notify when the auth token and user location have been acquired.
 *
 * @author Amilcar Serrano
 */
abstract class BaseActivity : AppCompatActivity() {

    //Method used to communicate when the token and user location have been obtained
    abstract fun listenForTokenAndLocation()

    //Observable that could be used on child views and would be disposed of automatically if used.
    protected lateinit var disposableObserver: Disposable

    //Location provider needed to find the user's last known location
    private lateinit var locationProvider: FusedLocationProviderClient

    //Observable used to requests the endpoint's token
    private val tokenObservable = NetworkService.getAPI().getToken(PrivateConstants.CLIENT_ID, PrivateConstants.CLIENT_SECRET)

    //Observable used to notify when the user's location has been acquired
    @SuppressLint("MissingPermission")
    private val locationObservable: Observable<Location> = Observable.create<Location> { emitter ->

        //Checks if the user granted location permissions to the app
        if (hasLocationPermission()) {
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
        } else {
            //The user didn't grant location permissions, we'll initialize the app with a dummy location
            val dummyLocation = Location("")
            dummyLocation.latitude = -34.90369
            dummyLocation.longitude = -56.19264

            emitter.onNext(dummyLocation)
            emitter.onComplete()
        }
    }

    //Observable that zip's the tokenObservable and locationObservable to retrieve their results once both of them have emitted
    protected val tokenAndLocationObservable: Observable<AuthLocation> = Observable.zip(
        tokenObservable,
        locationObservable,
        BiFunction<Token, Location, AuthLocation> { token: Token, location: Location ->
            return@BiFunction AuthLocation(
                token.accessToken,
                location
            )
        })

    //Reacts to the user's response to the request for location permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    listenForTokenAndLocation()
                } else {
                    showExplanationDialog()
                }
                return
            }
        }
    }

    //Verifies if the user has granted location permissions or not
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        if (savedInstanceState == null) {
            //We request the location permission and explain why we need them if needed, only if the app doesn't have location permissions
            if (!hasLocationPermission()) {
                //Verifies if we should show a permission or an explanation to the user
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showExplanationDialog()
                } else {
                    requestPermission()
                }
            } else {
                //Notifies the child activities they can start requesting the token and user's location because we got the user's permission
                listenForTokenAndLocation()
            }
        }
    }

    private fun showExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.location_request_title))
            .setMessage(getString(R.string.location_request_message))
            .setPositiveButton(getString(R.string.yes_please)) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                requestPermission()
            }.setNegativeButton(getString(R.string.no_thanks)) { _: DialogInterface, _: Int -> listenForTokenAndLocation() }
            .show()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (::disposableObserver.isInitialized) {
            disposableObserver.dispose()
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST = 1
    }
}