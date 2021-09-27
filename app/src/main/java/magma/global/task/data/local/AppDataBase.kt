package magma.global.task.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import magma.global.task.data.local.repository.dao.FeedDao
import magma.global.task.model.Feed

@Database(
    entities = [Feed::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): FeedDao
}