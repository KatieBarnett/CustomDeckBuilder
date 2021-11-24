package dev.katiebarnett.customdeckbuilder.data.storage

import androidx.room.*
import dev.katiebarnett.customdeckbuilder.models.Deck
import dev.katiebarnett.customdeckbuilder.models.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckBuilderDao {

    // Games
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_GAMES + " ORDER BY name")
    fun getAllGames(): Flow<List<Game>>
    
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_GAMES + " WHERE id = :gameId")
    fun getGame(gameId: Long): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg game: Game?): List<Long>

    @Delete
    suspend fun delete(game: Game)


    // Decks
    @Query("SELECT * FROM " + DatabaseConstants.TABLE_DECKS + " WHERE gameId = :gameId ORDER BY name")
    fun getDecksForGame(gameId: Long): Flow<List<Deck>>
//    
//    @Query("SELECT * FROM " + DatabaseConstants.TABLE_DECKS + " WHERE id = :deckId")
//    fun getDeck(deckId: Long): LiveData<List<Deck>>
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg deck: Deck?): List<Long>
//
//    @Delete
//    suspend fun delete(deck: Deck)
//
//
//    // Cards
//    @Query("SELECT * FROM " + DatabaseConstants.TABLE_CARDS + " WHERE deckId = :deckId ORDER BY name")
//    fun getCardsForDeck(deckId: Long): LiveData<List<Card>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(vararg card: Card?): LiveData<List<Long>>
//
//    @Delete
//    suspend fun delete(card: Card)
    
}