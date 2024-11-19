package com.example.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.model.WeatherResponse

class LocationAdapter(
    private var locations: List<WeatherResponse>,
    private val onLocationClick: (WeatherResponse) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val weatherResponse = locations[position]
        holder.bind(weatherResponse, onLocationClick)
    }

    fun updateData(newWeatherResponses: List<WeatherResponse>) {
        locations = newWeatherResponses
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = locations.size

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationName: TextView = itemView.findViewById(R.id.locationName)
        private val conditionIcon: ImageView = itemView.findViewById(R.id.conditionIcon)
        private val highLowTemp: TextView = itemView.findViewById(R.id.highLowTemperature)
        private val locationCurrentTemp: TextView = itemView.findViewById(R.id.locationCurrentTemp)

        fun bind(weatherResponse: WeatherResponse, onClick: (WeatherResponse) -> Unit) {
            locationName.text = weatherResponse.location.name
            highLowTemp.text = "H: ${weatherResponse.forecast.forecastday[0].day.maxtemp_c}°C | L: ${weatherResponse.forecast.forecastday[0].day.mintemp_c}°C"
            locationCurrentTemp.text = "${weatherResponse.current.temp_c}°C"

            val iconUrl = "https:${weatherResponse.current.condition.icon}"
            Glide.with(itemView.context).load(iconUrl).into(conditionIcon)

            itemView.setOnClickListener { onClick(weatherResponse) }
        }
    }
}