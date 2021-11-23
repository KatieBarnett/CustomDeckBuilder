package dev.katiebarnett.customdeckbuilder.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.katiebarnett.customdeckbuilder.data.storage.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_CARDS)
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    var lastModified: Long = System.currentTimeMillis(),
    val name: String,
    val imageUrl: String)