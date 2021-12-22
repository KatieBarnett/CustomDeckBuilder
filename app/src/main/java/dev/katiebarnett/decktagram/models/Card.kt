package dev.katiebarnett.decktagram.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.katiebarnett.decktagram.data.storage.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_CARDS)
data class Card(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="cardId")
    val id: Long = 0,
    @ColumnInfo(name="deckIdMap")
    val deckId: Long,
    @ColumnInfo(name="cardLastModified")
    var lastModified: Long = System.currentTimeMillis(),
    @ColumnInfo(name="cardName")
    val name: String,
    @ColumnInfo(name="cardImageUrl")
    val imageUrl: String)