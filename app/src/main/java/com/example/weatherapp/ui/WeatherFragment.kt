package com.example.weatherapp.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.model.Day
import com.example.weatherapp.model.WeatherResponse

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWeatherBinding.bind(view)

        // Retrieve the WeatherResponse from the fragment arguments
        val weatherResponse: WeatherResponse? = arguments?.getParcelable("weather_response")

        weatherResponse?.let {
            binding.locationName.text = it.location.name
            binding.currentTemperature.text = "${it.current.temp_c}"
            binding.weatherCondition.text = it.current.condition.text
            binding.highLowTemperature.text = "H: ${it.forecast.forecastday[0].day.maxtemp_c} +  |  L: ${it.forecast.forecastday[0].day.mintemp_c}"


            binding.tenDayForecastRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//            val dayAdapter = DayForecastAdapter(it.forecast.forecastday) { selectedDay ->
//                onItemClicked(selectedDay)
//            }
//            binding.tenDayForecastRecyclerView.adapter = dayAdapter
        }
    }

    fun onItemClicked(selectedDay: Day) {
        val dialog = MoreDetailsDialogFragment()
        // Pass data to the dialog, if needed, using a Bundle
        val bundle = Bundle()
        bundle.putParcelable("selected_day", selectedDay) // You can pass any data you want
        dialog.arguments = bundle

        // Use parentFragmentManager to show the dialog from a fragment
        dialog.show(parentFragmentManager, "more_details_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}