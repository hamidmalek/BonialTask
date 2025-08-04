package com.example.bonialtask.ui.brochure

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrochureTopAppBar(
    hasPermission: Boolean,
    locationOnState: State<Boolean>,
    filterActive: Boolean,
    onToggleFilter: () -> Unit
) {
    val btnState = when {
        !hasPermission || !locationOnState.value -> ButtonState.NotAvailable
        filterActive -> ButtonState.Active
        else -> ButtonState.Inactive
    }

    TopAppBar(
        title = { Text("Brochures", style = MaterialTheme.typography.headlineSmall) },
        actions = {
            LocationFilterButton(state = btnState, onClick = onToggleFilter)
        }
    )
}