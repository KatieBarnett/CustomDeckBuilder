package dev.katiebarnett.decktagram.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.katiebarnett.decktagram.data.storage.DeckBuilderDao
import dev.katiebarnett.decktagram.data.storage.DeckBuilderDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDeckBuilderDatabase(@ApplicationContext context: Context): DeckBuilderDatabase {
        return DeckBuilderDatabase.getInstance(context)
    }

    @Provides
    fun provideDeckBuilderDao(deckBuilderDatabase: DeckBuilderDatabase): DeckBuilderDao {
        return deckBuilderDatabase.deckBuilderDao()
    }
}
