package com.example.weatherapp.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.RetrofitClient
import com.example.weatherapp.network.WeatherApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainViewModelFactory(
    private val application: Application,
    private val weatherApiService: WeatherApiService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, weatherApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainViewModel(application: Application, private val weatherApiService: WeatherApiService = RetrofitClient.apiService) : AndroidViewModel(application) {

    private val _weatherResponses = MutableLiveData<List<WeatherResponse>>()
    val weatherResponses: LiveData<List<WeatherResponse>> get() = _weatherResponses
    var currentLocation: String = ""

    // Internal mutable LiveData for locations
    private val _storedLocations = MutableLiveData<MutableList<String>>()
    // Public LiveData to observe the stored locations
    val storedLocations: LiveData<MutableList<String>> get() = _storedLocations

    private val preferences = application.getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)

    // Moshi setup
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // Create a Type for a MutableList of Strings for Moshi
    private val listType = Types.newParameterizedType(MutableList::class.java, String::class.java)
    private val jsonAdapter = moshi.adapter<MutableList<String>>(listType)

    init {
        loadStoredLocations()
    }

    fun fetchWeatherData(locations: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val apiService = weatherApiService
            val responses = mutableListOf<WeatherResponse>()
            try {
                val locationsWithCurrent = if (currentLocation.isNotEmpty()) {
                    listOf(currentLocation) + locations
                } else {
                    locations
                }
                for (location in locationsWithCurrent) {
                    val response = apiService.getWeatherByLocation(location.toString())
                    responses.add(response)
                }
                withContext(Dispatchers.Main) {
                    _weatherResponses.value = responses
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun addLocation(location: String) {
        val updatedLocations = _storedLocations.value ?: mutableListOf()
        if (!updatedLocations.contains(location)) {
            updatedLocations.add(location)
            // Only update the LiveData if the list has actually changed
            if (_storedLocations.value != updatedLocations) {
                _storedLocations.value = updatedLocations // Update LiveData
            }
            saveLocationsToStorage()
            fetchWeatherData(updatedLocations) // Fetch data for the new location
        }
    }

    fun removeLocation(location: String) {
        val updatedLocations = _storedLocations.value?.toMutableList() ?: mutableListOf()
        if (updatedLocations.contains(location)) {
            updatedLocations.remove(location)
            // Only update the LiveData if the list has actually changed
            if (_storedLocations.value != updatedLocations) {
                _storedLocations.value = updatedLocations // Update LiveData
            }
            saveLocationsToStorage()
            fetchWeatherData(updatedLocations) // Fetch data for the new location
        }
    }

    private fun saveLocationsToStorage() {
        try {
            val json = jsonAdapter.toJson(_storedLocations.value)
            preferences.edit().putString("locations", json).apply()
        } catch (e: IOException) {
            Toast.makeText(getApplication(), "Error saving locations", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadStoredLocations() {
        val json = preferences.getString("locations", null)
        if (json != null) {
            try {
                val locations: MutableList<String>? = jsonAdapter.fromJson(json)
                _storedLocations.value = locations ?: mutableListOf()
            } catch (e: IOException) {
                _storedLocations.value = mutableListOf()
            }
        } else {
            _storedLocations.value = mutableListOf()
        }
    }

    fun setStoredLocationsTestOnly(locations: MutableList<String>) {
        _storedLocations.value = locations
    }
}