package com.codesgood.restaurants.data.model

import android.location.Location

/**
 * Object returned by the Zip Observable in BaseActivity#tokenAndLocationObservable.
 *
 * @param token Authorization token for API requests retrieved by BaseActivity#tokenObservable.
 * @param location User's location retrieved by BaseActivity#locationObservable.
 *
 * @author Amilcar Serrano
 */
data class AuthLocation(val token: String, val location: Location)