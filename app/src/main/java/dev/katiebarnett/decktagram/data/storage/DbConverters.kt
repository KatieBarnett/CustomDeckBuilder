package dev.katiebarnett.decktagram.data.storage

import androidx.room.TypeConverter
import com.squareup.moshi.Types
import dev.katiebarnett.decktagram.util.MoshiJsonParser

class DbConverters {

    inline fun <reified T> fromJson(value: String): T? {
        val jsonAdapter = MoshiJsonParser.moshi.adapter(T::class.java)
        return jsonAdapter.fromJson(value)
    }

    inline fun <reified T> toJson(value: T): String {
        val jsonAdapter = MoshiJsonParser.moshi.adapter(T::class.java)
        return jsonAdapter.toJson(value)
    }
    
    inline fun <reified T> fromJsonToList(values: String?): List<T>? {
        if (values == null) {
            return null
        }
        val type = Types.newParameterizedType(List::class.java, T::class.java)
        val adapter = MoshiJsonParser.moshi.adapter<List<T>>(type)
        return adapter.fromJson(values)
    }

    inline fun <reified T> toJsonFromList(values: List<T>?): String? {
        if (values == null) {
            return null
        }
        val type = Types.newParameterizedType(List::class.java, T::class.java)
        val adapter = MoshiJsonParser.moshi.adapter<List<T>>(type)
        return adapter.toJson(values)
    }

    @TypeConverter
    fun fromLongList(values: List<Long>?): String? = toJsonFromList(values)

    @TypeConverter
    fun toLongList(values: String?): List<Long>? = fromJsonToList(values)
}