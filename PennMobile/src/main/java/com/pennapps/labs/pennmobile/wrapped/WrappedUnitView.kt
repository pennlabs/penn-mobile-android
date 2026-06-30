package com.pennapps.labs.pennmobile.wrapped

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@Composable
fun WrappedUnitView(
    unit: WrappedUnit,
    preloadedComposition: LottieComposition?,
    pageOffset: Float,
    progressFraction: Float
) {
    val dynamicProperties = rememberLottieDynamicProperties(
        *unit.values.map { (key, value) ->
            rememberLottieDynamicProperty(
                property = LottieProperty.TEXT,
                value = value,
                "**", key, "**"
            )
        }.toTypedArray()
    )

    LottieAnimation(
        composition = preloadedComposition,
        progress = { progressFraction },
        dynamicProperties = dynamicProperties,
        fontMap = mapOf(
            "Poppins-Bold" to android.graphics.Typeface.DEFAULT_BOLD,
            "Poppins-Medium" to android.graphics.Typeface.DEFAULT,
            "Poppins-SemiBold" to android.graphics.Typeface.DEFAULT,
            "Poppins-Regular" to android.graphics.Typeface.DEFAULT
        ),
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = -(pageOffset * 45f)
                cameraDistance = 12f * density
                transformOrigin = TransformOrigin(
                    pivotFractionX = if (pageOffset > 0) 0f else 1f,
                    pivotFractionY = 0.5f
                )
            }
    )
}
