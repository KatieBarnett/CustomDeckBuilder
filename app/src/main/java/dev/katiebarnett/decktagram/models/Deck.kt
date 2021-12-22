package dev.katiebarnett.decktagram.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.katiebarnett.decktagram.data.storage.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_DECKS)
data class Deck(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="deckId")
    val id: Long = 0,
    @ColumnInfo(name="gameIdMap")
    val gameId: Long,
    @ColumnInfo(name="deckLastModified")
    var lastModified: Long = System.currentTimeMillis(),
    @ColumnInfo(name="deckName")
    val name: String,
    @ColumnInfo(name="deckImageUrl")
    val imageUrl: String? = null
)