package com.example.points_manager.di

import android.content.Context
import androidx.room.Room
import com.example.points_manager.data.RepositoryImpl
import com.example.points_manager.data.db.AppDatabase
import com.example.points_manager.data.db.daoses.PointDao
import com.example.points_manager.domain.Repository
import com.example.points_manager.domain.usecases.GetPointsUseCase
import com.example.points_manager.domain.usecases.GetPointsUseCaseImpl
import com.example.points_manager.domain.usecases.SavePointUseCase
import com.example.points_manager.domain.usecases.SavePointUseCaseImpl
import com.example.points_manager.presentation.dialogs.CreatePointDialogFragment
import com.example.points_manager.presentation.fragments.MapFragment
import com.example.points_manager.presentation.fragments.PointsFragment
import com.example.points_manager.presentation.services.TcpServerServiceCoroutine
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(fragment: MapFragment)
    fun inject(fragment: CreatePointDialogFragment)
    fun inject(pointsFragment: PointsFragment)
    fun inject(tcpServerServiceCoroutine: TcpServerServiceCoroutine)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}

@Module(includes = [DataModule::class, BindingModule::class])
class AppModule

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideRoom(ctx: Context): AppDatabase {
        return Room.databaseBuilder(ctx, AppDatabase::class.java, "database.db")
            .build()
    }

    @Provides
    fun providePointDao(database: AppDatabase): PointDao {
        return database.getPointDao()
    }
}

@Module
interface BindingModule {
    @Binds
    fun bindRepositoryToRepositoryImpl(repositoryImpl: RepositoryImpl): Repository

    @Binds
    fun bindSavePointUseCaseToSavePointUseCaseImpl(savePointUseCaseImpl: SavePointUseCaseImpl): SavePointUseCase

    @Binds
    fun bindGetPointUseCaseToGetPointUseCaseImpl(getPointsUseCaseImpl: GetPointsUseCaseImpl): GetPointsUseCase
}