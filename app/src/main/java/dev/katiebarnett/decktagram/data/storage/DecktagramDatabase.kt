package dev.katiebarnett.decktagram.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.katiebarnett.decktagram.models.Card
import dev.katiebarnett.decktagram.models.Deck
import dev.katiebarnett.decktagram.models.Game
import dev.katiebarnett.decktagram.models.PersistedDeckState

@Database(entities = [
    Game::class,
    Deck::class,
    Card::class,
    PersistedDeckState::class], version = 1, exportSchema = true)
@TypeConverters(DbConverters::class)
abstract class DecktagramDatabase: RoomDatabase() {
    
    abstract fun deckBuilderDao(): DecktagramDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: DecktagramDatabase? = null

        fun getInstance(context: Context): DecktagramDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): DecktagramDatabase {
            return Room.databaseBuilder(
                context,
                DecktagramDatabase::class.java,
                DatabaseConstants.DATABASE_DECK_BUILDER
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

