package com.codesgood.restaurants.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * NetworkServices serves a window to the backend endpoints by holding a Singleton reference to a RestaurantAPI.
 *
 * @author Amilcar Serrano
 */
object NetworkService {

    //Endpoint's base url
    private const val BASE_URL = " http://stg-api.pedidosya.com/public/v1/"

    //RestAPI that allows us to executo HTTP requests
    private lateinit var REST_API: RestaurantAPI

    //Singleton method, initializes the API and/or returns it
    fun getAPI(): RestaurantAPI {
        if (!::REST_API.isInitialized) {
            generateAPI()
        }

        return REST_API
    }

    //Responsible of setting up the API taking the headers in account if they have been fetched
    fun generateAPI(authHeader: String? = null) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        if (authHeader != null) {
            httpClient.addInterceptor {
                val original = it.request()

                //Endpoint's headers
                val request = original.newBuilder()
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build()

                it.proceed(request)
            }
        }

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()

        REST_API = retrofit.create(RestaurantAPI::class.java)
    }
}