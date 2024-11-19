package com.example.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.model.Day
import com.example.weatherapp.model.ForecastDay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DayForecastAdapter(private val forecastList: List<ForecastDay>) : RecyclerView.Adapter<DayForecastAdapter.DayForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_forecast, parent, false)
        return DayForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayForecastViewHolder, position: Int) {
        val forecast = forecastList[position]

        // Set the day name (Mon, Tue, etc.)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Input date format (yyyy-MM-dd)
        val outputFormat = SimpleDateFormat("EEE", Locale.getDefault()) // Output format for weekday (e.g. Mon, Tue, etc.)

        // Get the current date in the same format as forecast.date (yyyy-MM-dd)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


        // Parse the forecast date string into a Date object
        val date = dateFormat.parse(forecast.date)

        // Check if the forecast date is today
        val dayName = if (forecast.date == currentDate) {
            "Today" // If the forecast date matches today's date, show "Today"
        } else {
            outputFormat.format(date) // Otherwise, show the abbreviated weekday name (Mon, Tue, etc.)
        }

        // Set the day name
        holder.dayName.text = dayName // Ex: "Mon", "Tue"

        // Set the condition icon using Picasso or Glide (from the URL)
        val iconUrl = "https:${forecast.day.condition.icon}"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.conditionIcon) // Load the image into the ImageView

        // Set the high/low temperatures
        holder.temperature.text = "H: ${forecast.day.maxtemp_c}°C L: ${forecast.day.mintemp_c}°C"
    }

    override fun getItemCount(): Int = forecastList.size

    inner class DayForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayName: TextView = itemView.findViewById(R.id.dayName)
        val conditionIcon: ImageView = itemView.findViewById(R.id.conditionIcon)
        val temperature: TextView = itemView.findViewById(R.id.temperature)
    }
}