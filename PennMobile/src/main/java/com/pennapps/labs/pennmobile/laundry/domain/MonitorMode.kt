package com.pennapps.labs.pennmobile.laundry.domain

/**
 * Which machine type the background monitor is watching, if any.
 *
 * Owns its own persisted representation so the "OFF"/"WASHERS"/"DRYERS" literals live in one place
 * instead of being repeated across the fragment, the worker and SharedPreferences.
 */
enum class MonitorMode(
    val prefValue: String,
    val machineLabel: String,
) {
    OFF("OFF", ""),
    WASHERS("WASHERS", "washer"),
    DRYERS("DRYERS", "dryer"),
    ;

    companion object {
        /** Anything unrecognised, including null, means "not monitoring". */
        fun fromPrefValue(value: String?): MonitorMode = entries.firstOrNull { it.prefValue == value } ?: OFF
    }
}
