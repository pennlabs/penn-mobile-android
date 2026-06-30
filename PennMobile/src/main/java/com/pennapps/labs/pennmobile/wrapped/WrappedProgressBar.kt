package com.pennapps.labs.pennmobile.wrapped

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun WrappedProgressBar(
    pageCount: Int,
    currentPageIndex: Int,
    currentPageProgress: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (index in 0 until pageCount) {
            val segmentProgress =
                when {
                    index < currentPageIndex -> 1f
                    index == currentPageIndex -> currentPageProgress
                    else -> 0f
                }

            LinearProgressIndicator(
                progress = { segmentProgress },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
            )
        }
    }
}
