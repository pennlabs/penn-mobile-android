package com.pennapps.labs.pennmobile.classes
import android.graphics.Bitmap
import android.os.Environment
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object MultipartUtil {

    fun createPartFromBitmap(bitmap: Bitmap): MultipartBody.Part {
        // Convert bitmap to file
        val bitmapFile = convertBitmapToFile(bitmap)

        // Create RequestBody instance from file
        val requestFile = bitmapFile.asRequestBody("image/*".toMediaType())

        // Create MultipartBody.Part instance
        return MultipartBody.Part.createFormData("image", bitmapFile.name, requestFile)
    }

    private fun convertBitmapToFile(bitmap: Bitmap): File {
        // Create a file to save the bitmap
        val file = File(Environment.getExternalStorageDirectory(), "image.jpg")
        try {
            // Compress the bitmap to JPEG format
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun createSubletPart(subletId: Int): MultipartBody.Part {
        // Convert the integer subletId to a string
        val subletString = subletId.toString()

        // Create the request body from the string
        val subletRequestBody = subletString.toRequestBody("text/plain".toMediaType())

        // Create the multipart part
        return MultipartBody.Part.createFormData("sublet", null, subletRequestBody)
    }
}
