package com.example.bonialtask.ui.brochure

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.bonialtask.model.ContentWrapper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun LoadedList(data: List<ContentWrapper.Brochure>) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 2

    val hasPermission = ContextCompat
        .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    val locationOnState = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val lm = context.getSystemService<LocationManager>()
        locationOnState.value = (lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true)
                || (lm?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true)
    }

    var filterActive by rememberSaveable { mutableStateOf(false) }

    var userLocation by rememberSaveable { mutableStateOf<android.location.Location?>(null) }

    val settingsClient = LocationServices.getSettingsClient(context)
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000L
    )
        .build()
    val settingsRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .build()
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {

    }

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            locationOnState.value = true
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { loc ->
                userLocation = loc
                filterActive = !filterActive
            }
                .addOnFailureListener {
                    filterActive = !filterActive
                }
        }
    }

    LaunchedEffect(hasPermission, locationOnState.value) {
        if (hasPermission && locationOnState.value) {
            try {
                fusedClient
                    .lastLocation
                    .addOnSuccessListener { loc -> userLocation = loc }
            } catch (_: SecurityException) {
                userLocation = null
            }
        }
    }
    Scaffold(
        topBar = {
            BrochureTopAppBar(
                hasPermission = hasPermission,
                locationOnState = locationOnState,
                filterActive = filterActive,
                onToggleFilter = {
                    if (!hasPermission) {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    } else if (!locationOnState.value) {
                        settingsClient.checkLocationSettings(settingsRequest)
                            .addOnSuccessListener { filterActive = !filterActive }
                            .addOnFailureListener { exc ->
                                if (exc is ResolvableApiException) {
                                    settingsLauncher.launch(
                                        IntentSenderRequest.Builder(exc.resolution.intentSender)
                                            .build()
                                    )
                                }
                            }
                    } else {
                        filterActive = !filterActive
                    }
                }
            )
        }
    ) { innerPadding ->
        BrochureGrid(
            data = data,
            filterActive = filterActive,
            userLocation = userLocation,
            columns = columns,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
