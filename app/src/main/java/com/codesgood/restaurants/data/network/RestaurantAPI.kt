package com.codesgood.restaurants.data.network

import com.codesgood.restaurants.data.model.RestaurantResponse
import com.codesgood.restaurants.data.model.Token
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST endpoints the app is able to reach
 *
 * @author Amilcar Serrano
 */
interface RestaurantAPI {

    /**
     * Requests the auth token needed by other endpoint calls
     *
     * @param clientId Client's id, NOT AVAILABLE IN REPO
     * @param clientSecret Client's secret, NOT AVAILABLE IN REPO
     */
    @GET("tokens")
    fun getToken(
        @Query("clientId") clientId: String,
        @Query("clientSecret") clientSecret: String
    ): Observable<Token>

    /**
     * Requests a list of restaurants near the desired coordinates
     *
     * @param point Coordinates the endpoint will use to look for restaurants nearby
     * @param country Coordinate's country
     * @param max Maximum amount of restaurants in the list
     */
    @GET("search/restaurants")
    fun fetchRestaurants(
        @Query("point") point: String,
        @Query("country") country: Int,
        @Query("max") max: Int,
        @Query("offset") offset: Int
    ): Observable<RestaurantResponse>
}