package magma.global.task.data.remote.services

import magma.global.task.data.remote.responses.NearbySearchResponse
import magma.global.task.utils.Urls
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface INewsService {

    @GET(Urls.NEARBY_PLACES)
    suspend fun getNearbyPlaces(
        @Query("type") type: String?,
        @Query("location") location: String?,
        @Query("radius") radius: Int,
        @Query("key") apiToken: String?
    ): Response<NearbySearchResponse>

    @GET(Urls.TEXT_SEARCH)
    suspend fun getPlaceDetailsByTitleAndLocation(
        @Query("query") type: String?,
        @Query("location") location: String?,
        @Query("key") apiToken: String?
    ): Response<NearbySearchResponse>

}