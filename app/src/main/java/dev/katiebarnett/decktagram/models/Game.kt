package dev.katiebarnett.decktagram.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.katiebarnett.decktagram.data.storage.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_GAMES)
data class Game(        
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="gameId")
    val id: Long = 0,
    @ColumnInfo(name="gameLastModified")
    var lastModified: Long = System.currentTimeMillis(),
    @ColumnInfo(name="gameName")
    val name: String,
    @ColumnInfo(name="gameImageUrl")
    val imageUrl: String? = null)