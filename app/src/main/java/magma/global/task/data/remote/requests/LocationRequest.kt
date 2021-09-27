package magma.global.task.data.remote.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LocationRequest {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null
}