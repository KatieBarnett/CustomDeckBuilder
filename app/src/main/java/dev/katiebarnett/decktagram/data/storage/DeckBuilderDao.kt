package dev.katiebarnett.decktagram.data.storage

import androidx.room.*
import dev.katiebarnett.decktagram.models.Card
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.models.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckBuilderDao {

    // Games
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_GAMES + " ORDER BY gameName")
    fun getAllGames(): Flow<List<Game>>
    
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_GAMES + " WHERE gameId = :gameId")
    fun getGame(gameId: Long): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg game: Game?): List<Long>

    @Delete
    suspend fun delete(game: Game)
    
    // Decks
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_DECKS + " WHERE gameIdMap = :gameId ORDER BY deckName")
    fun getDecksForGame(gameId: Long): Flow<List<Deck>>
    
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_DECKS + " WHERE deckId = :deckId")
    fun getDeck(deckId: Long): Flow<Deck>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg deck: Deck?): List<Long>

    @Delete
    suspend fun delete(deck: Deck)
    
    // Cards
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_CARDS + " WHERE cardId = :cardId")
    fun getCard(cardId: Long): Flow<List<Card>>
    
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_CARDS + " WHERE deckIdMap = :deckId")
    fun getCardsForDeck(deckId: Long): Flow<List<Card>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg card: Card?): List<Long>

    @Delete
    suspend fun delete(card: Card)
    
}