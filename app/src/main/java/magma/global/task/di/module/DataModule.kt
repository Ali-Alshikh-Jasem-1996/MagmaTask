package magma.global.task.di.module

import magma.global.task.data.repository.DataRepository
import magma.global.task.data.repository.DataSource
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun provideDataRepository(dataRepository: DataRepository): DataSource
}