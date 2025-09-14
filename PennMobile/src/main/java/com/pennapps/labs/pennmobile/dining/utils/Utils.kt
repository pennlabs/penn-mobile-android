package com.pennapps.labs.pennmobile.dining.utils

fun smoothBalances(values: List<Float>): List<Float> {
    if (values.size < 3) return values  // need at least 3 points to smooth

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
