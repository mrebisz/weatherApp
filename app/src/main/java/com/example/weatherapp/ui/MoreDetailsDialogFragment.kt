package com.example.weatherapp.ui

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherapp.R

class MoreDetailsDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the dialog
        val view = inflater.inflate(R.layout.dialog_more_details, container, false)

        // Here you can add more details or additional functionality

        return view
    }
}