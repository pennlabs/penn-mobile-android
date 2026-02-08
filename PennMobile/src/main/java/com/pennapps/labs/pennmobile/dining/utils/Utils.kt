package com.pennapps.labs.pennmobile.dining.utils

// Avoids random spikes in data
fun smoothBalances(values: List<Float>): List<Float> {
    if (values.size < 3) return values

    val smoothed = values.toMutableList()
    for (i in 1 until values.size - 1) {
        val left = smoothed[i - 1]
        val right = smoothed[i + 1]
        val current = smoothed[i]

        if (current < right) {
            smoothed[i] = (left + right) / 2f
        }
    }

    return smoothed
}
