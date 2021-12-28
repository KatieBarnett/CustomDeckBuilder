package dev.katiebarnett.decktagram.data.repositories

import dev.katiebarnett.decktagram.data.storage.DecktagramDao
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.models.DeckState
import dev.katiebarnett.decktagram.models.PersistedDeckState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StateRepository @Inject constructor(
    private val decktagramDao: DecktagramDao
) {

    fun getDeckState(deckId: Long): PersistedDeckState? {
        return decktagramDao.getDeckState(deckId).firstOrNull()
    }

    fun getGameState(gameId: Long): List<PersistedDeckState> {
        return decktagramDao.getGameState(gameId)
    }
    
    suspend fun updateDeckState(deck: Deck, deckState: DeckState): Long {
        val state = PersistedDeckState (
            deckId = deck.id,
            gameId = deck.gameId,
            drawnCards = deckState.drawnCards.map { it.id },
            remainingCards = deckState.remainingCards.map { it.id },
            lastModified = deckState.lastModified
        )
        val primaryKey = decktagramDao.insert(state)
        return primaryKey.firstOrNull() ?: throw Throwable("Error updating deck state")
    }
    
    suspend fun deleteDeckState(deckState: PersistedDeckState) {
        decktagramDao.delete(deckState)
    }
}