package magma.global.task.data.local.repository

import magma.global.task.data.local.repository.dao.FeedDao
import magma.global.task.model.Feed
import javax.inject.Inject

class LocalRepository
@Inject constructor(
    private val feedDao: FeedDao
) {
    fun updateFeedItem(deletedDate: Long, description: String) {
        return feedDao.update(description, deletedDate)
    }

    fun insertFeedsList(feeds: ArrayList<Feed>): LongArray {
        return feedDao.insertAll(feeds)
    }

    fun insertFeedItem(feed: Feed) {
        return feedDao.insert(feed)
    }

    fun getDeletedFeed(): List<Feed> {
        return feedDao.getDeletedNews()
    }

    fun deletePermanently(item: Feed) {
        feedDao.deletePermanently(item)
    }

}