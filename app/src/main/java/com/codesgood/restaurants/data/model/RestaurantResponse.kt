package com.codesgood.restaurants.data.model

import androidx.lifecycle.ViewModel

data class RestaurantResponse(
    val total: Int = 0,
    val max: Int = 0,
    val sort: String? = null,
    val count: Int = 0,
    val data: ArrayList<Restaurant> = ArrayList(),
    val offset: Int = 0
) : ViewModel()