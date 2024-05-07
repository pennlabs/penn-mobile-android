package com.pennapps.labs.pennmobile.utils

import android.content.Context
import android.util.TypedValue
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Created by Davies Lumumba on 8/12/20. */
object Utils {
    /** Get current formatted system time */
    @JvmStatic
    fun getCurrentSystemTime(): String {
        return SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(
            Date(),
        ).toUpperCase(Locale.getDefault())
    }

    /**
     * Converts dps to pixels nicely.
     *
     * @param context the Context for getting the resources
     * @param dp dimension in dps
     * @return dimension in pixels
     */
    @JvmStatic
    fun dpToPixel(
        context: Context,
        dp: Float,
    ): Int {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return try {
            (dp * (metrics.densityDpi / 160f)).toInt()
        } catch (ignored: NoSuchFieldError) {
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                metrics,
            ).toInt()
        }
    }

    /**
     * Returns screen width.
     *
     * @param context Context to get resources and device specific display metrics
     * @return screen width
     */
    @JvmStatic
    fun getScreenWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return (displayMetrics.widthPixels / displayMetrics.density).toInt()
    }

    /** Get color from id.  */
    @JvmStatic
    fun getColor(
        context: Context,
        color: Int,
    ): Int {
        val tv = TypedValue()
        context.theme.resolveAttribute(color, tv, true)
        return tv.data
    }

    fun getSha256Hash(codeVerifier: String): String {
        // Hash the code verifier
        val md = MessageDigest.getInstance("SHA-256")
        val byteArr = md.digest(codeVerifier.toByteArray())

        // Base-64 encode
        var codeChallenge =
            Base64.getEncoder().encodeToString(byteArr)

        // Replace characters to make it web safe
        codeChallenge = codeChallenge.replace("=", "")
        codeChallenge = codeChallenge.replace("+", "-")
        codeChallenge = codeChallenge.replace("/", "_")

        return codeChallenge
    }
}
