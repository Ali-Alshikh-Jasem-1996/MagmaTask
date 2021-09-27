package magma.global.task.data.local.repository.dao

import androidx.room.*
import magma.global.task.model.Feed

@Dao
interface FeedDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(feeds: ArrayList<Feed>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(feed: Feed)

    @Query("UPDATE Feed SET deleted =:deleted, deletedDate=:deletedDate  WHERE description =:description")
    fun update(description: String,deletedDate : Long, deleted: Boolean = true)

    @Query("SELECT * FROM Feed WHERE deleted =:deleted")
    fun getDeletedNews(deleted: Boolean = true): List<Feed>

    @Delete
    fun deletePermanently(item: Feed)

}