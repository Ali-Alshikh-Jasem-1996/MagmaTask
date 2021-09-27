package magma.global.task.data.remote.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import magma.global.task.data.remote.requests.LocationRequest

class NearbySearchResponse {

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any> = ArrayList()

    @SerializedName("next_page_token")
    @Expose
    var nextPageToken: String? = null

    @SerializedName("results")
    @Expose
    var results: List<Result> = ArrayList()

    @SerializedName("status")
    @Expose
    var status: String? = null
}

class Result {

    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("opening_hours")
    @Expose
    var openingHours: OpeningHours? = null

    @SerializedName("photos")
    @Expose
    var photos: List<Photo> = java.util.ArrayList<Photo>()

    @SerializedName("place_id")
    @Expose
    var placeId: String? = null

    @SerializedName("rating")
    @Expose
    var rating: Double? = null

    @SerializedName("reference")
    @Expose
    var reference: String? = null

    @SerializedName("scope")
    @Expose
    var scope: String? = null

    @SerializedName("types")
    @Expose
    var types: List<String> = java.util.ArrayList()

    @SerializedName("vicinity")
    @Expose
    var vicinity: String? = null

    @SerializedName("price_level")
    @Expose
    var priceLevel: Int? = null
}

class Geometry {
    @SerializedName("location")
    @Expose
    var location: LocationRequest? = null
}

class Photo {

    @SerializedName("height")
    @Expose
    var height: Int? = null

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<String> = java.util.ArrayList()

    @SerializedName("photo_reference")
    @Expose
    var photoReference: String? = null

    @SerializedName("width")
    @Expose
    var width: Int? = null
}

class OpeningHours {

    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null

    @SerializedName("weekday_text")
    @Expose
    var weekdayText: List<Any> = java.util.ArrayList()
}