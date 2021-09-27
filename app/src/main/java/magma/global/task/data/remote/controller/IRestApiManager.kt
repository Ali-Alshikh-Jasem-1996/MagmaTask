package magma.global.task.data.remote.controller

import magma.global.task.data.remote.responses.NearbySearchResponse

internal interface IRestApiManager {

    suspend fun getNearbyPlaces(type: String, location: String): Resource<NearbySearchResponse>

    suspend fun getPlaceDetailsByTitleAndLocation(query: String, location: String): Resource<NearbySearchResponse>
}