package com.example.weatherapp.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentAddLocationBinding
import com.example.weatherapp.model.SearchLocation
import com.example.weatherapp.network.RetrofitClient
import com.example.weatherapp.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddLocationFragment : Fragment() {

    private var _binding: FragmentAddLocationBinding? = null
    private val binding get() = _binding!!

    private val locationList = mutableListOf<SearchLocation>()
    private lateinit var adapter: LocationSearchAdapter
    private lateinit var locationSelectListener: LocationSelectListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the parent activity implements the listener
        if (context is LocationSelectListener) {
            locationSelectListener = context
        } else {
            throw RuntimeException("$context must implement LocationSelectListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        adapter = LocationSearchAdapter(locationList) { selectedLocation ->
            onLocationSelected(selectedLocation)
        }
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResultsRecyclerView.adapter = adapter

        // Set up search bar listener
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = RetrofitClient.apiService
                val results = apiService.searchLocations(query)

                withContext(Dispatchers.Main) {
                    locationList.clear()
                    locationList.addAll(results)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to search locations: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onLocationSelected(location: SearchLocation) {
        val viewModel: MainViewModel by activityViewModels()
        viewModel.addLocation(location.name)
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}