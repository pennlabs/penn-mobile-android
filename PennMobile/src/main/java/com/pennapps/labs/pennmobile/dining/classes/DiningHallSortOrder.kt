package com.pennapps.labs.pennmobile.dining.classes

enum class DiningHallSortOrder(val key: String) {

    Residential("RESIDENTIAL"),
    Open("OPEN"),
    Name("NAME");

    fun toDisplayString(): String =
        key.lowercase().replaceFirstChar { it.uppercase() }

    companion object {
        fun fromKey(key: String?): DiningHallSortOrder {
            return DiningHallSortOrder.entries.find { it.key == key }
                ?: Residential
        }
    }

}