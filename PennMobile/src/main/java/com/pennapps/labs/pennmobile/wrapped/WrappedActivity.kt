package com.pennapps.labs.pennmobile.wrapped

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel

class WrappedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WrappedScreen(
                viewModel = viewModel<WrappedViewModel>(),
                onFinish = { finish() },
            )
        }
    }
}
