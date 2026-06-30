package com.example.penn_wrapped

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.penn_wrapped.WrappedScreen
import com.example.penn_wrapped.WrappedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WrappedScreen(
                viewModel = WrappedViewModel(),
                onFinish = { println("Wrapped experience finished") }
            )
        }
    }
}