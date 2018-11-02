package com.codesgood.restaurants.data.model

import com.google.gson.annotations.SerializedName

/**
 * Token's data class/model.
 *
 * @author Amilcar Serrano
 */
data class Token(@SerializedName("access_token") val accessToken: String)