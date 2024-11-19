package com.example.weatherapp.ui

import com.example.weatherapp.model.SearchLocation

interface LocationSelectListener {
    fun onLocationSelected(location: SearchLocation)
}