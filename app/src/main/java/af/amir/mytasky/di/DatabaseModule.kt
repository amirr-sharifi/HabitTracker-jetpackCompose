package af.amir.mytasky.di

import af.amir.mytasky.data.local.db.AppDatabase
import af.amir.mytasky.data.local.db.dao.DailyHabitDao
import af.amir.mytasky.data.local.db.dao.HabitDao
import af.amir.mytasky.data.local.prefrences.TaskyDataStorePreferencesManager
import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application) =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "taskAndHabitDb"
        ).build()


    @Provides
    @Singleton
    fun provideHabitDao(
        appDatabase: AppDatabase,
    ): HabitDao = appDatabase.habitDao

    @Provides
    @Singleton
    fun provideDailyHabitDao(
        appDatabase: AppDatabase,
    ): DailyHabitDao = appDatabase.dailyHabitDao



    @Provides
    @Singleton
    fun providePreference(
        @ApplicationContext
        context: Context,
    ): TaskyDataStorePreferencesManager {
        return TaskyDataStorePreferencesManager(context)
    }
}