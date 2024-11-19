package com.example.weatherapp.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Condition(
    val text: String,
    val icon: String,
    val code: Int
) : Parcelable