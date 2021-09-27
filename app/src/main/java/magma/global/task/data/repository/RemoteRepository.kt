package magma.global.task.data.repository

import android.util.Log
import com.google.gson.Gson
import magma.global.task.BuildConfig
import magma.global.task.data.remote.controller.ErrorManager
import magma.global.task.data.remote.controller.IRestApiManager
import magma.global.task.data.remote.controller.Resource
import magma.global.task.data.remote.controller.ServiceGenerator
import magma.global.task.data.remote.responses.NearbySearchResponse
import magma.global.task.data.remote.services.INewsService
import magma.global.task.utils.Const
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class RemoteRepository
@Inject constructor(private val serviceGenerator: ServiceGenerator, private val gson: Gson) :
    IRestApiManager {

    override suspend fun getNearbyPlaces(type: String, location: String): Resource<NearbySearchResponse> {
        val authService = serviceGenerator.createService(INewsService::class.java)
        try {
            Log.d("TAG", "Ksa getNearbyPlaces: $type")
            Log.d("TAG", "Ksa getNearbyPlaces: $location")
            Log.d("TAG", "Ksa getNearbyPlaces: "+Const.PROXIMITY_RADIUS)
            Log.d("TAG", "Ksa getNearbyPlaces: "+BuildConfig.MAPS_API_KEY)
            val response = authService.getNearbyPlaces(type, location, Const.PROXIMITY_RADIUS, BuildConfig.MAPS_API_KEY)

            return if (response.isSuccessful) {
                //Do something with response e.g show to the UI.
                Resource.Success(response.body() as NearbySearchResponse)
            } else {
                val errorBody = gson.fromJson(
                    response.errorBody()?.string(),
                    ErrorManager::class.java
                )
                errorBody.code = response.code()
                Resource.DataError(errorBody)
            }
        } catch (e: HttpException) {
            return Resource.Exception(e.message() as String)
        } catch (e: Throwable) {
            return Resource.Exception(errorMessage = e.message as String)
        } catch (e: SocketTimeoutException) {
            return Resource.Exception(errorMessage = e.message as String)
        } catch (e: IOException) {
            return Resource.Exception(errorMessage = e.message as String)
        }
    }

    override suspend fun getPlaceDetailsByTitleAndLocation(query: String, location: String): Resource<NearbySearchResponse> {
        val authService = serviceGenerator.createService(INewsService::class.java)
        try {
            Log.d("TAG", "Ksa getPlaceDetailsByTitleAndLocation: $location")
            Log.d("TAG", "Ksa getPlaceDetailsByTitleAndLocation: $query")
            Log.d("TAG", "Ksa getPlaceDetailsByTitleAndLocation: "+BuildConfig.MAPS_API_KEY)
            val response = authService.getPlaceDetailsByTitleAndLocation(query, location, BuildConfig.MAPS_API_KEY)

            return if (response.isSuccessful) {
                //Do something with response e.g show to the UI.
                Resource.Success(response.body() as NearbySearchResponse)
            } else {
                val errorBody = gson.fromJson(
                    response.errorBody()?.string(),
                    ErrorManager::class.java
                )
                errorBody.code = response.code()
                Resource.DataError(errorBody)
            }
        } catch (e: HttpException) {
            return Resource.Exception(e.message() as String)
        } catch (e: Throwable) {
            return Resource.Exception(errorMessage = e.message as String)
        } catch (e: SocketTimeoutException) {
            return Resource.Exception(errorMessage = e.message as String)
        } catch (e: IOException) {
            return Resource.Exception(errorMessage = e.message as String)
        }
    }

}