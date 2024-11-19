package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.model.SearchLocation
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.RetrofitClient
import com.example.weatherapp.ui.AddLocationFragment
import com.example.weatherapp.ui.LocationAdapter
import com.example.weatherapp.ui.LocationDetailsDialogFragment
import com.example.weatherapp.ui.LocationSelectListener
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.MainViewModelFactory
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity(), LocationSelectListener {

    private lateinit var locationRecyclerView: RecyclerView
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var addLocationButton: Button

    private val viewModel: MainViewModel by viewModels {
        // Initialize the factory with the required dependencies
        MainViewModelFactory(application, RetrofitClient.apiService)  // Pass your actual WeatherApiService instance here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupAddLocationButton()

        observeViewModel()

        checkLocationPermissionAndFetchData()
    }

    private fun setupRecyclerView() {
        locationRecyclerView = findViewById(R.id.locationRecyclerView)
        locationRecyclerView.layoutManager = LinearLayoutManager(this)
        locationAdapter = LocationAdapter(emptyList()) { weatherResponse ->
            onLocationClicked(weatherResponse)
        }
        locationRecyclerView.adapter = locationAdapter

        setupSwipeToRemove()
    }

    private fun setupSwipeToRemove() {
        // Add swipe-to-delete functionality using ItemTouchHelper
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // No drag-and-drop
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Allow swiping both directions
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // We are not implementing drag functionality
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                // Prevent the first item (current location) from being swiped
                if (position == 0) {
                    locationAdapter.notifyItemChanged(position) // Reset swipe effect
                    Toast.makeText(this@MainActivity, "Current location cannot be removed", Toast.LENGTH_SHORT).show()
                    return
                }

                // Proceed with removing the item
                val location = viewModel.storedLocations.value?.get(position - 1) // Adjust index because the first item is not in storedLocations
                if (location != null) {
                    viewModel.removeLocation(location)
                    Toast.makeText(this@MainActivity, "$location removed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(locationRecyclerView)
    }

    private fun setupAddLocationButton() {
        addLocationButton = findViewById(R.id.addLocationButton)
        addLocationButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddLocationFragment())
                .addToBackStack(null)
                .commit()
        }
        updateAddLocationButtonVisibility()
    }

    private fun observeViewModel() {
        viewModel.weatherResponses.observe(this) { responses ->
            locationAdapter.updateData(responses)
        }
        viewModel.storedLocations.observe(this) { locations ->
            updateAddLocationButtonVisibility() // Update visibility when locations change
        }
    }

    private fun checkLocationPermissionAndFetchData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchWeatherData()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchWeatherData()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWeatherData() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.currentLocation = "${location.latitude},${location.longitude}"
                viewModel.storedLocations.value?.toList()?.takeIf { it.isNotEmpty() }?.let {
                    viewModel.fetchWeatherData(it)
                } ?: viewModel.fetchWeatherData(emptyList())
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Location error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onLocationClicked(weatherResponse: WeatherResponse) {
        val dialog = LocationDetailsDialogFragment.newInstance(weatherResponse)
        dialog.show(supportFragmentManager, "location_details_dialog")
    }

    private fun updateAddLocationButtonVisibility() {
        addLocationButton.visibility = if ((viewModel.storedLocations.value?.size ?: 0) < 4) View.VISIBLE else View.GONE
    }

    // On add location tap
    override fun onLocationSelected(location: SearchLocation) {
        val fullLocation = "${location.name}, ${location.country}, ${location.region}"
        viewModel.addLocation(fullLocation)
        Toast.makeText(this, "Location added: ${location.name}", Toast.LENGTH_SHORT).show()
    }
}