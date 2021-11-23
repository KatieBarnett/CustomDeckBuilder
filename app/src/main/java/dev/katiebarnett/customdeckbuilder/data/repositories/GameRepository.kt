package dev.katiebarnett.customdeckbuilder.data.repositories

import dev.katiebarnett.customdeckbuilder.data.storage.DeckBuilderDao
import dev.katiebarnett.customdeckbuilder.models.Game
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val deckBuilderDao: DeckBuilderDao
) {

    fun getAllGames() = deckBuilderDao.getAllGames()
    
//    suspend fun getAllGames(): Resource<List<Game>> {
//        return coroutineScope {
//            try {
//                withContext(Dispatchers.IO) {
//                    SuccessResource<List<Game>>(data = deckBuilderDao.getAllGames())
//                }
//            } catch (e: Exception) {
//                ErrorResource<List<Game>>(error = e)
//            }
//        }
//    }
//

    suspend fun updateGame(game: Game): Long {
        game.lastModified = System.currentTimeMillis()
        val primaryKey = deckBuilderDao.insert(game)
        return primaryKey.firstOrNull() ?: throw Throwable("Error creating game")
    }

    suspend fun getGame(id: Long): Game? {
        return deckBuilderDao.getGame(id).firstOrNull()
    }
//
//    suspend fun removeGame(game: Game): Resource<Boolean> {
//        return coroutineScope {
//            try {
//                withContext(Dispatchers.IO) {
//                    deckBuilderDao.delete(game)
//                    SuccessResource<Boolean>(data = true)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                ErrorResource<Boolean>(error = e)
//            }
//        }
//    }
}