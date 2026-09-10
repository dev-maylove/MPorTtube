package com.mporttube.di

import android.content.Context
import androidx.room.Room
import com.mporttube.data.local.AppDatabase
import com.mporttube.data.local.DownloadDao
import com.mporttube.data.local.FavoriteDao
import com.mporttube.data.local.HistoryDao
import com.mporttube.data.local.PlaylistDao
import com.mporttube.data.local.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mporttube.db"
        )
            // V5 used schema version 1 without migrations. During development,
            // destructive migration avoids a crash when upgrading old local builds.
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideVideoDao(db: AppDatabase): VideoDao = db.videoDao()
    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()
    @Provides fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()
    @Provides fun providePlaylistDao(db: AppDatabase): PlaylistDao = db.playlistDao()
    @Provides fun provideDownloadDao(db: AppDatabase): DownloadDao = db.downloadDao()
}
