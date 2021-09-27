package magma.global.task.presentation.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import magma.global.task.data.remote.controller.Resource
import magma.global.task.data.remote.responses.NearbySearchResponse
import magma.global.task.data.repository.DataRepository
import magma.global.task.utils.Event
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MapsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    override val coroutineContext: CoroutineContext,
) : ViewModel(), CoroutineScope {
    val nearbyPlacesResponse = MutableLiveData<Event<Resource<NearbySearchResponse>>>()
    val placeResponse = MutableLiveData<Event<Resource<NearbySearchResponse>>>()

    private val TAG = "MapsViewModel"

    fun getNearbyRestaurants(type: String, location: Location?) {
        launch {
            if (location != null)
                nearbyPlacesResponse.value = Event(Resource.Loading())

            val fullLocation = location?.latitude.toString() + "," + location?.longitude.toString()
            Log.d(TAG, "Omar getNearbyRestaurants: $fullLocation")
            val serviceResponse: Resource<NearbySearchResponse> =
                dataRepository.getNearbyPlaces(type, fullLocation)
            nearbyPlacesResponse.value = Event(serviceResponse)
            Log.d(TAG, "Omar getNearbyRestaurants: ${serviceResponse.response}")
        }
    }

    fun getPlaceDetailsByTitleAndLocation(query: String, location: LatLng) {
        launch {
            placeResponse.value = Event(Resource.Loading())
            val fullLocation = location.latitude.toString() + "," + location.longitude.toString()
            Log.d(TAG, "Omar getPlaceDetailsByTitleAndLocation: $query")
            Log.d(TAG, "Omar getPlaceDetailsByTitleAndLocation: $fullLocation")
            val serviceResponse: Resource<NearbySearchResponse> =
                dataRepository.getPlaceDetailsByTitleAndLocation(query, fullLocation)
            placeResponse.value = Event(serviceResponse)
            Log.d(TAG, "Omar getPlaceDetailsByTitleAndLocation: ${serviceResponse.response}")
        }
    }
}