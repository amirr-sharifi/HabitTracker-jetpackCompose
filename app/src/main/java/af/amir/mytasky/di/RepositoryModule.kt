package af.amir.mytasky.di

import af.amir.mytasky.data.repository.DailyHabitRepositoryImpl
import af.amir.mytasky.data.repository.HabitRepositoryImpl
import af.amir.mytasky.domain.repository.DailyHabitRepository
import af.amir.mytasky.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindsHabitRepository(
        repositoryImpl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    abstract fun bindsDailyHabitRepository(
        repositoryImpl: DailyHabitRepositoryImpl
    ): DailyHabitRepository


}