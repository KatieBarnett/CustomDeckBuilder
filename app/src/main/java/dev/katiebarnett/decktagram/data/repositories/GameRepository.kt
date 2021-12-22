package dev.katiebarnett.decktagram.data.repositories

import dev.katiebarnett.decktagram.data.storage.DeckBuilderDao
import dev.katiebarnett.decktagram.models.Card
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.models.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val deckBuilderDao: DeckBuilderDao
) {

    fun getAllGames(): Flow<List<Game>> {
        return deckBuilderDao.getAllGames()
    }
    
    suspend fun updateGame(game: Game): Long {
        game.lastModified = System.currentTimeMillis()
        val primaryKey = deckBuilderDao.insert(game)
        return primaryKey.firstOrNull() ?: throw Throwable("Error updating game")
    }

    fun getGame(id: Long): Flow<Game> {
        return deckBuilderDao.getGame(id).map { 
            it.firstOrNull() ?: throw Throwable("Error getting game")
        }
    }
    
    suspend fun deleteGame(game: Game) {
        deckBuilderDao.delete(game)
    }
    
    fun getDecksForGame(gameId: Long): Flow<List<Deck>> {
        return deckBuilderDao.getDecksForGame(gameId)
    }

    fun getDeck(id: Long): Flow<Map<Deck, List<Card>>> {
        return deckBuilderDao.getDeck(id)
    }

    suspend fun deleteDeck(deck: Deck) {
        deckBuilderDao.delete(deck)
    }

    suspend fun updateDeck(deck: Deck): Long {
        deck.lastModified = System.currentTimeMillis()
        val primaryKey = deckBuilderDao.insert(deck)
        return primaryKey.firstOrNull() ?: throw Throwable("Error updating deck")
    }

    suspend fun updateCard(deckId: Long, cardName: String, cardPath: String): Long {
        // Add card to database
        val card = Card(
            lastModified = System.currentTimeMillis(),
            name = cardName, 
            imageUrl = cardPath,
            deckId = deckId
        )
        val primaryKey = deckBuilderDao.insert(card)
        return primaryKey.firstOrNull() ?: throw Throwable("Error updating card")
    }

    suspend fun deleteCard(card: Card) {
        deckBuilderDao.delete(card)
    }
}