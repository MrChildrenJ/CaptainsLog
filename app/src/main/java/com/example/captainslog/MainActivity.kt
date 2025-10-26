package com.example.captainslog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.captainslog.ui.MainScreen
import com.example.captainslog.ui.theme.CaptainsLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaptainsLogTheme {
                MainScreen()
            }
        }
    }
}