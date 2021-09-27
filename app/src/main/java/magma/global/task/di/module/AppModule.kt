package magma.global.task.di.module

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import magma.global.task.utils.Const
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import magma.global.task.data.local.AppDatabase
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


@Module
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        Const.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideNewsDao(db: AppDatabase) = db.newsDao()



    @Provides
    @Singleton
    fun provideCoroutineContext(): CoroutineContext {
        return Dispatchers.Main
    }

    @Provides
    @Singleton
    fun provideGsonObject(): Gson {
        return Gson()
    }
}