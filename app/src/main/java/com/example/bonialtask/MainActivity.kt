package com.example.bonialtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bonialtask.ui.nav.NavGraph
import com.example.bonialtask.ui.theme.BonialTaskTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BonialTaskTheme {
                NavGraph()
            }
        }
    }
}
