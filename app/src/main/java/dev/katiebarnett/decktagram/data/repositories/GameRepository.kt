package dev.katiebarnett.decktagram.data.repositories

import dev.katiebarnett.decktagram.data.storage.DecktagramDao
import dev.katiebarnett.decktagram.models.Card
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.models.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val decktagramDao: DecktagramDao
) {

    fun getAllGames(): Flow<List<Game>> {
        return decktagramDao.getAllGames()
    }
    
    suspend fun updateGame(game: Game): Long {
        game.lastModified = System.currentTimeMillis()
        val primaryKey = decktagramDao.insert(game)
        return primaryKey.firstOrNull() ?: throw Throwable("Error updating game")
    }

    fun getGame(id: Long): Flow<Game> {
        return decktagramDao.getGame(id).map { 
            it.firstOrNull() ?: throw Throwable("Error getting game")
        }
    }
    
    suspend fun deleteGame(game: Game) {
        decktagramDao.delete(game)
    }
    
    fun getDecksForGame(gameId: Long): Flow<List<Deck>> {
        return decktagramDao.getDecksForGame(gameId)
    }

    fun getDeck(id: Long): Flow<Deck> {
        return decktagramDao.getDeck(id)
    }

    fun getCardsForDeck(deckId: Long): Flow<List<Card>> {
        return decktagramDao.getCardsForDeck(deckId)
    }

    suspend fun deleteDeck(deck: Deck) {
        decktagramDao.delete(deck)
    }

    suspend fun updateDeck(deck: Deck): Long {
        deck.lastModified = System.currentTimeMillis()
        val primaryKey = decktagramDao.insert(deck)
        return primaryKey.firstOrNull() ?: throw Throwable("Error updating deck")
    }

    suspend fun updateCard(deckId: Long, cardName: String? = null, cardPath: String): Long {
        val card = Card(
            lastModified = System.currentTimeMillis(),
            name = cardName, 
            imageUrl = cardPath,
            deckId = deckId
        )
        val primaryKey = decktagramDao.insert(card)
        return primaryKey.firstOrNull() ?: throw Throwable("Error updating card")
    }

    suspend fun deleteCard(card: Card) {
        decktagramDao.delete(card)
    }
}