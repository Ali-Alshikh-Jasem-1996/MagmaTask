package magma.global.task.data.repository

import magma.global.task.data.local.repository.LocalRepository
import magma.global.task.data.remote.controller.Resource
import magma.global.task.data.remote.responses.NearbySearchResponse
import magma.global.task.model.Feed
import javax.inject.Inject

class DataRepository
@Inject
constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository
) : DataSource {

    override suspend fun getNearbyPlaces(type: String, location: String): Resource<NearbySearchResponse> {
        return remoteRepository.getNearbyPlaces(type, location)
    }

    override suspend fun getPlaceDetailsByTitleAndLocation(query: String, location: String): Resource<NearbySearchResponse> {
        return remoteRepository.getPlaceDetailsByTitleAndLocation(query, location)
    }

    override fun updateNewsItem(deletedDate: Long,description: String) {
        localRepository.updateFeedItem(deletedDate,description)
    }

    override fun getDeletedNews() : List<Feed>{
        return localRepository.getDeletedFeed()
    }

    override fun deletePermanently(item: Feed) {
        localRepository.deletePermanently(item)
    }

    override fun insertFeedList(feeds: ArrayList<Feed>) {
        localRepository.insertFeedsList(feeds)
    }

    override fun insertFeedItem(feed: Feed) {
        localRepository.insertFeedItem(feed)

    }

}

