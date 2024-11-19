package com.example.weatherapp.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
) : Parcelable