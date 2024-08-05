package com.example.gpermission

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var locationPermissionTextView: TextView
    private lateinit var latTextView: TextView
    private lateinit var lngTextView: TextView
    private lateinit var startButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationLayout: LinearLayout

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onLocationPermissionGranted()
        } else {
            onLocationPermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationPermissionTextView = findViewById(R.id.location_permission)
        latTextView = findViewById(R.id.lat)
        lngTextView = findViewById(R.id.lng)
        startButton = findViewById(R.id.start_button)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationLayout = findViewById(R.id.location_layout)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Thêm log để kiểm tra hàm checkLocationPermission được gọi
        Log.d("MainActivity", "Calling checkLocationPermission")
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                onLocationPermissionGranted()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Show an explanation to the user why the permission is needed, then request the permission
                requestLocationPermission()
            }
            else -> {
                // Directly request for the permission
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun onLocationPermissionGranted() {
        runOnUiThread {
            Log.d("MainActivity", "Location permission granted")
            locationPermissionTextView.visibility = TextView.VISIBLE
            locationPermissionTextView.text = "Granted"
            locationPermissionTextView.setTextColor(resources.getColor(android.R.color.holo_green_dark, theme))
            startButton.isEnabled = true
            startButton.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark, theme))
            startButton.setOnClickListener {
                Log.d("MainActivity", "Start button clicked")
                getCurrentLocation()
            }
        }
    }

    private fun onLocationPermissionDenied() {
        runOnUiThread {
            Log.d("MainActivity", "Location permission denied")
            locationPermissionTextView.visibility = TextView.VISIBLE
            locationPermissionTextView.text = "Not granted"
            locationLayout.visibility = LinearLayout.GONE
            locationPermissionTextView.setTextColor(resources.getColor(android.R.color.holo_red_dark, theme))
            startButton.isEnabled = false
            startButton.setBackgroundColor(resources.getColor(android.R.color.darker_gray, theme))
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        runOnUiThread {
                            locationLayout.visibility = LinearLayout.VISIBLE
                            latTextView.text = "Lat: ${location.latitude}"
                            lngTextView.text = "Lng: ${location.longitude}"
                        }
                    }
                }
        }
    }
}
