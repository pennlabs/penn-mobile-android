package com.pennapps.labs.pennmobile.components.floatingbottombar.utils

import android.os.Build

typealias Scope = () -> Unit

internal inline fun applyForApiLAndHigher(scope: Scope) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        scope()
    }
}
