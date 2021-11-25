package dev.katiebarnett.decktagram.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.katiebarnett.decktagram.data.storage.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_DECKS)
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    var lastModified: Long = System.currentTimeMillis(),
    val name: String,
    val imageUrl: String? = null)