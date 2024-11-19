package com.example.weatherapp.network

import com.example.weatherapp.model.SearchLocation
import com.example.weatherapp.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

interface WeatherApiService {

    @GET("forecast.json")
    suspend fun getWeatherByLocation(
        @Query("q") location: String,
        @Query("days") days: Int = 10, // Default to 10 days
        @Query("key") apiKey: String = "a0f5326554e64af9a81144117241211" // Hardcoded API key
    ): WeatherResponse

    @GET("search.json")
    suspend fun searchLocations(
        @Query("q") query: String,
        @Query("key") apiKey: String = "a0f5326554e64af9a81144117241211"
    ): List<SearchLocation>

    companion object {
        fun create(): WeatherApiService {
            // Create a Moshi instance with Kotlin adapter
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/v1/") // Base URL of the weather API
                .addConverterFactory(MoshiConverterFactory.create(moshi)) // Moshi converter
                .build()

            return retrofit.create(WeatherApiService::class.java)
        }
    }
}