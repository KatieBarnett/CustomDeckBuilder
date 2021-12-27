package dev.katiebarnett.decktagram.models

data class ImageQuality(
    val ratioHeight: Int? = DEFAULT_RATIO_HEIGHT, 
    val ratioWidth: Int? = DEFAULT_RATIO_WIDTH
) {
    companion object {
        const val DEFAULT_RATIO_HEIGHT = 640
        const val DEFAULT_RATIO_WIDTH = 480
        val DEVICE_DEFAULT = null
    }

    override fun toString(): String {
        return "$ratioHeight,$ratioWidth"
    }
}