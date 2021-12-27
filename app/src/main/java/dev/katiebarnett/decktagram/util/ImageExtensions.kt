package dev.katiebarnett.decktagram.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.IOException

private const val BYTES_IN_MEGABYTE = 1024*1024

fun Bitmap.reduceImageToSize(megaBytes: Float): ByteArray {
    val baseArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 80, baseArrayOutputStream)
    var imageBytes = baseArrayOutputStream.toByteArray()
    var scaleDivider = 1
    while (imageBytes.size > megaBytes * BYTES_IN_MEGABYTE && scaleDivider < 10) {
        scaleDivider++
        val scaleWidth: Int = this.width / scaleDivider
        val scaleHeight: Int = this.height / scaleDivider
        try {
            imageBytes = getDownsizedImageBytes(this, scaleWidth, scaleHeight)
        } catch (e: IOException) {
            // Do nothing, just return what we have
            return imageBytes
        }
    }
    return imageBytes
}

@Throws(IOException::class)
fun getDownsizedImageBytes(fullBitmap: Bitmap, scaleWidth: Int, scaleHeight: Int): ByteArray {
    val scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true)
    val baseArrayOutputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baseArrayOutputStream)
    return baseArrayOutputStream.toByteArray()
}