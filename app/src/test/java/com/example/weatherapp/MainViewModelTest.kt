package com.example.weatherapp.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    // Mock dependencies
    @Mock private lateinit var application: Application
    @Mock private lateinit var sharedPreferences: SharedPreferences
    @Mock private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    @Mock private lateinit var weatherApiService: WeatherApiService  // Mocked WeatherApiService
    @Mock private lateinit var weatherObserver: Observer<List<WeatherResponse>>
    @Mock private lateinit var locationsObserver: Observer<MutableList<String>>

    // Coroutine dispatcher for testing
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MainViewModel

    // LiveData test rule to ensure correct threading
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mock shared preferences behavior
        whenever(application.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
        whenever(sharedPreferencesEditor.putString(any(), any())).thenReturn(sharedPreferencesEditor)

        // Set up the MainViewModel with the mocked WeatherApiService
        viewModel = MainViewModel(application, weatherApiService)  // Inject mocked API service
        Dispatchers.setMain(testDispatcher)


    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset dispatcher to avoid interference
    }

    @Test
    fun `fetchWeatherData successfully fetches and updates weather responses`() = runTest {
        // Mock Location data for New York and London
        val newYorkLocation = Location(
            name = "New York",
            region = "NY",
            country = "USA",
            lat = 40.7128,
            lon = -74.0060,
            tz_id = "America/New_York",
            localtime_epoch = 1636661225,
            localtime = "2024-11-19 12:00:00"
        )
        val londonLocation = Location(
            name = "London",
            region = "London",
            country = "UK",
            lat = 51.5074,
            lon = -0.1278,
            tz_id = "Europe/London",
            localtime_epoch = 1636661225,
            localtime = "2024-11-19 12:00:00"
        )

        // Mock API response with the Location data
        val newYorkWeatherResponse = WeatherResponse(
            location = newYorkLocation,
            current = mock(),  // Use mock for current weather object
            forecast = mock()  // Use mock for forecast object
        )
        val londonWeatherResponse = WeatherResponse(
            location = londonLocation,
            current = mock(),  // Mock current weather data
            forecast = mock()  // Mock forecast data
        )

        // Mock API responses for the two locations
        whenever(weatherApiService.getWeatherByLocation(eq("New York"), any(), any())).thenReturn(newYorkWeatherResponse)
        whenever(weatherApiService.getWeatherByLocation(eq("London"), any(), any())).thenReturn(londonWeatherResponse)

        // Attach observer to LiveData
        viewModel.weatherResponses.observeForever(weatherObserver)

        // Trigger the fetchWeatherData function
        viewModel.fetchWeatherData(listOf("New York", "London"))

        // Advance coroutines until all tasks are completed
        advanceUntilIdle()

        // Verify the API calls are made correctly for both locations
        verify(weatherApiService, times(2)).getWeatherByLocation(any(), any(), any()) // Should call the API twice (for New York and London)
    }

    @Test
    fun `addLocation adds location to stored locations and fetches weather data`() = runTest {
        // Mock initial stored locations
        val initialLocations = mutableListOf("New York")
        viewModel.storedLocations.observeForever(locationsObserver)
        viewModel.setStoredLocationsTestOnly(initialLocations)

        // Add a new location
        viewModel.addLocation("London")

        // Verify stored locations updated.
        verify(locationsObserver, times(1)).onChanged(mutableListOf("New York", "London"))

        // Clear the observer to avoid further notifications
        viewModel.storedLocations.removeObserver(locationsObserver)

        // Verify data was fetched for the new location
        verify(sharedPreferencesEditor).putString(eq("locations"), any())
    }

    @Test
    fun `removeLocation removes location from stored locations`() = runTest {
        // Mock initial stored locations
        val initialLocations = mutableListOf("New York", "London")
        viewModel.setStoredLocationsTestOnly(initialLocations)
        viewModel.storedLocations.observeForever(locationsObserver)

        // Remove a location
        viewModel.removeLocation("London")

        // Verify the stored locations were updated, ensure only 1 onChanged call with the expected list
        verify(locationsObserver, times(1)).onChanged(mutableListOf("New York"))

        // Clear the observer to avoid further notifications
        viewModel.storedLocations.removeObserver(locationsObserver)

        // Verify that SharedPreferences are updated
        verify(sharedPreferencesEditor).putString(eq("locations"), any())
    }

    @Test
    fun `loadStoredLocations initializes stored locations from shared preferences`() {
        // Mock shared preferences JSON for locations
        val mockJson = "[\"New York\", \"London\"]"
        whenever(sharedPreferences.getString("locations", null)).thenReturn(mockJson)

        // Set the preferences to the ViewModel manually (normally, this would happen via the constructor)
        viewModel.loadStoredLocations()

        // Observe storedLocations LiveData
        val observer = Observer<MutableList<String>> { locations ->
            // Check if the stored locations are loaded correctly
            Assert.assertNotNull(locations)
            Assert.assertEquals(listOf("New York", "London"), locations)
        }

        // Register the observer for storedLocations LiveData
        viewModel.storedLocations.observeForever(observer)

        // Trigger loadStoredLocations() and check the result
        viewModel.loadStoredLocations()

        // Verify the stored locations in the LiveData
        val storedLocations = viewModel.storedLocations.value
        Assert.assertNotNull(storedLocations)
        Assert.assertEquals(listOf("New York", "London"), storedLocations)

        // Unregister the observer to prevent memory leaks
        viewModel.storedLocations.removeObserver(observer)
    }

    @Test
    fun `fetchWeatherData handles API errors gracefully`() = runTest {
        // Mock API failure
        whenever(weatherApiService.getWeatherByLocation(any(), any(), any())).thenThrow(RuntimeException("API failure"))

        // Attach observer
        viewModel.weatherResponses.observeForever(weatherObserver)

        // Trigger function
        viewModel.fetchWeatherData(listOf("InvalidLocation"))

        // Advance coroutines
        advanceUntilIdle()

        // Verify no weather data was updated
        verify(weatherObserver, never()).onChanged(any())
    }
}