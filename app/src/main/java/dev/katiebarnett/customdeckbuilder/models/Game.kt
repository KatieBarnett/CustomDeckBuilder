package dev.katiebarnett.customdeckbuilder.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.katiebarnett.customdeckbuilder.data.storage.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_GAMES)
data class Game(        
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var lastModified: Long = System.currentTimeMillis(),
    val name: String,
    val imageUrl: String? = null)