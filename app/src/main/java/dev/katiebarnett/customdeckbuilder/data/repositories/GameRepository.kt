package dev.katiebarnett.customdeckbuilder.data.repositories

import dev.katiebarnett.customdeckbuilder.data.storage.DeckBuilderDao
import dev.katiebarnett.customdeckbuilder.models.Deck
import dev.katiebarnett.customdeckbuilder.models.Game
import kotlinx.coroutines.flow.Flow
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
        return primaryKey.firstOrNull() ?: throw Throwable("Error creating game")
    }

    fun getGame(id: Long): Flow<List<Game>> {
        return deckBuilderDao.getGame(id)
    }
    
    fun getDecksForGame(gameId: Long): Flow<List<Deck>> {
        return deckBuilderDao.getDecksForGame(gameId)
    }

    suspend fun updateDeck(deck: Deck): Long {
        deck.lastModified = System.currentTimeMillis()
        val primaryKey = deckBuilderDao.insert(deck)
        return primaryKey.firstOrNull() ?: throw Throwable("Error creating deck")
    }
}