package com.pennapps.labs.pennmobile.components.floatingbottombar.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SavedState(
    val selectedItem: Int,
    val superState: Parcelable?,
) : Parcelable
