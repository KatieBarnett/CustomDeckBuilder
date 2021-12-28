package dev.katiebarnett.decktagram.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.katiebarnett.decktagram.data.storage.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_DECK_STATES)
data class PersistedDeckState(
    @PrimaryKey(autoGenerate = false)
    val deckId: Long = 0,
    val gameId: Long = 0,
    val drawnCards: List<Long>,
    val remainingCards: List<Long>,
    var lastModified: Long
)