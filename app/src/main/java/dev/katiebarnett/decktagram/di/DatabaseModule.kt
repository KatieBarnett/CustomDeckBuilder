package dev.katiebarnett.decktagram.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.katiebarnett.decktagram.data.storage.DecktagramDao
import dev.katiebarnett.decktagram.data.storage.DecktagramDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDecktagramDatabase(@ApplicationContext context: Context): DecktagramDatabase {
        return DecktagramDatabase.getInstance(context)
    }

    @Provides
    fun provideDecktagramDao(decktagramDatabase: DecktagramDatabase): DecktagramDao {
        return decktagramDatabase.deckBuilderDao()
    }
}
