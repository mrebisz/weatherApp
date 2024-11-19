package com.example.weatherapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    val name: String?,
    val latitude: Double?,
    val longitude: Double?,
    var days: Int
) : Parcelable