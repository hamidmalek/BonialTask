package com.example.bonialtask.ui.brochure

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationFilterButton(
    state: ButtonState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.small
    ) {
        val (icon, label) = when (state) {
            ButtonState.NotAvailable -> Icons.Filled.LocationDisabled to "Not Active"
            ButtonState.Inactive -> Icons.Filled.LocationOn to "5 km off"
            ButtonState.Active -> Icons.Filled.LocationOff to "5 km on"
        }

        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(4.dp))
        Text(label)
    }
}

enum class ButtonState {
    NotAvailable,
    Inactive,
    Active
}