package com.example.weatherapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapp.model.WeatherResponse

class WeatherPagerAdapter(fragmentActivity: FragmentActivity, private val weatherResponses: List<WeatherResponse>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = weatherResponses.size

    override fun createFragment(position: Int): Fragment {
        val fragment = WeatherFragment()
        fragment.arguments = Bundle().apply {
            Log.d("weatherPagerAdapter", "weatherResponses: $weatherResponses")
            putParcelable("weather_response", weatherResponses[position])
        }
        return fragment
    }
}