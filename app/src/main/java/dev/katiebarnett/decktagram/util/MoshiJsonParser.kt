package dev.katiebarnett.decktagram.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiJsonParser {

    val moshi = provideMoshi()

    private fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    fun <T> fromJson(jsonString: String, objectClass: Class<T>): T? {
        return moshi.adapter<T>(objectClass).fromJson(jsonString)
    }

    fun <T> toJson(jsonObject: T, objectClass: Class<T>): String {
        return moshi.adapter<T>(objectClass).toJson(jsonObject)
    }

}