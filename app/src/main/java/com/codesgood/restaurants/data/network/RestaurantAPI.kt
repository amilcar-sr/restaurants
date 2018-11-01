package com.codesgood.restaurants.data.network

import com.codesgood.restaurants.data.model.RestaurantResponse
import com.codesgood.restaurants.data.model.Token
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RestaurantAPI {

    @GET("tokens")
    fun getToken(
        @Query("clientId") clientId: String,
        @Query("clientSecret") clientSecret: String
    ): Observable<Token>

    @GET("search/restaurants")
    fun fetchRestaurants(
        @Query("point") point: String,
        @Query("country") country: Int,
        @Query("max") max: Int,
        @Query("offset") offset: Int
    ): Observable<RestaurantResponse>
}