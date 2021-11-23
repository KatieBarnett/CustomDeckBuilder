package dev.katiebarnett.customdeckbuilder.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.katiebarnett.customdeckbuilder.models.Card
import dev.katiebarnett.customdeckbuilder.models.Deck
import dev.katiebarnett.customdeckbuilder.models.Game

@Database(entities = [
    Game::class,
    Deck::class,
    Card::class], version = 1, exportSchema = true)
//@TypeConverters(DbConverters::class)
abstract class DeckBuilderDatabase: RoomDatabase() {
    
    abstract fun deckBuilderDao(): DeckBuilderDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: DeckBuilderDatabase? = null

        fun getInstance(context: Context): DeckBuilderDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): DeckBuilderDatabase {
            return Room.databaseBuilder(
                context,
                DeckBuilderDatabase::class.java,
                DatabaseConstants.DATABASE_DECK_BUILDER
            )
            .build()
        }
    }
}

