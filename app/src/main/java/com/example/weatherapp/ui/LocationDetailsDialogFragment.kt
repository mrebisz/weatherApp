package com.example.weatherapp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentLocationDetailsBinding
import com.example.weatherapp.model.WeatherResponse

class LocationDetailsDialogFragment : DialogFragment() {

    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_WEATHER_RESPONSE = "weather_response"

        fun newInstance(weatherResponse: WeatherResponse): LocationDetailsDialogFragment {
            val fragment = LocationDetailsDialogFragment()
            val args = Bundle().apply {
                putParcelable(ARG_WEATHER_RESPONSE, weatherResponse)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_WeatherApp)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the WeatherResponse data from the arguments
        val weatherResponse: WeatherResponse? = arguments?.getParcelable(ARG_WEATHER_RESPONSE)
        binding.tenDayForecastRecyclerView.isNestedScrollingEnabled = false
        binding.infoGridRecyclerView.isNestedScrollingEnabled = false

        // Use the WeatherResponse to populate the UI
        weatherResponse?.let {
            // Populate location name
            binding.locationName.text = it.location.name
            // Populate current temperature
            binding.currentTemperature.text = "${it.current.temp_c}°C"
            // Populate weather condition
            binding.weatherCondition.text = it.current.condition.text
            // Populate high/low temperatures
            binding.highLowTemperature.text = "H: ${it.forecast.forecastday[0].day.maxtemp_c}°C | L: ${it.forecast.forecastday[0].day.mintemp_c}°C"

            // Set up the 10-day forecast RecyclerView
            binding.tenDayForecastRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            val dayAdapter = DayForecastAdapter(it.forecast.forecastday)
            binding.tenDayForecastRecyclerView.adapter = dayAdapter

            // Set up the info grid RecyclerView
            val infoList = listOf(
                InfoBoxData("Wind", "${it.current.wind_kph} km/h"),
                InfoBoxData("Sunrise / Sunset", "${it.forecast.forecastday[0].astro.sunrise} | ${it.forecast.forecastday[0].astro.sunset}" ),
                InfoBoxData("Dew Point", "${it.current.dewpoint_c}°C"),
                InfoBoxData("Humidity", "${it.current.humidity}%"),
                InfoBoxData("Precipitation", "${it.current.precip_mm} mm"),
                InfoBoxData("UV Index", "${it.current.uv}")
            )

            binding.infoGridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
            val infoAdapter = InfoBoxAdapter(infoList)
            binding.infoGridRecyclerView.adapter = infoAdapter
        }

        // Set an OnClickListener for the close button
        binding.closeButton.setOnClickListener {
            dismiss()  // Dismiss the dialog when the button is clicked
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}