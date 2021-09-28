package magma.global.task.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.AndroidInjection
import magma.global.task.BuildConfig
import magma.global.task.R
import magma.global.task.data.remote.controller.Resource
import magma.global.task.data.remote.responses.NearbySearchResponse
import magma.global.task.databinding.ActivityMapsBinding
import magma.global.task.utils.Const
import magma.global.task.utils.EventObserver
import magma.global.task.utils.ViewModelFactory
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var fabMyLocation: FloatingActionButton
    private lateinit var fabNearbyRestaurants: FloatingActionButton
    private val listMarkerOptions = arrayListOf<MarkerOptions>()

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    private var dummyDataPlaces = ArrayList<String>()

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MapsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MapsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        binding = ActivityMapsBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        setContentView(binding.root)
        supportActionBar?.hide()

        setUpDropDownMenu()
        setupObservers()

        fabMyLocation = binding.fabMyLocation
        fabNearbyRestaurants = binding.fabNearbyRestaurants

        // Construct a PlacesClient
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Build the map.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setUpDropDownMenu() {
        val edtSearch = binding.edtSearch
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line, dummyDataPlaces
        )
        edtSearch.setAdapter(adapter)

        dummyDataPlaces = ArrayList(listOf(*resources.getStringArray(R.array.places)))

        //Grand Kadri Hotel By Cristal Lebanon
        listMarkerOptions.add(
            MarkerOptions().position(LatLng(33.85148430277257, 35.895525763213946))
                .title(dummyDataPlaces[0])
        )
        //Germanos - Pastry
        listMarkerOptions.add(
            MarkerOptions().position(LatLng(33.85217073479985, 35.89477838111461))
                .title(dummyDataPlaces[1])
        )
        //Malak el Tawook
        listMarkerOptions.add(
            MarkerOptions().position(LatLng(33.85334017189446, 35.89438946093824))
                .title(dummyDataPlaces[2])
        )
        //Z Burger House
        listMarkerOptions.add(
            MarkerOptions().position(LatLng(33.85454300475094, 35.894561122304474))
                .title(dummyDataPlaces[3])
        )
        //Collège Oriental
        listMarkerOptions.add(
            MarkerOptions().position(LatLng(33.85129821373707, 35.89446263654391))
                .title(dummyDataPlaces[4])
        )
        //VERO MODA
        listMarkerOptions.add(
            MarkerOptions().position(LatLng(33.85048738635312, 35.89664059012788))
                .title(dummyDataPlaces[5])
        )

        //val adapter = AutoCompleteAdapter(this, listMarkerOptions)
        //edtSearch.setAdapter(adapter)

        edtSearch.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            moveToSearchPlace(position, selected)
        }

    }

    private fun moveToSearchPlace(position: Int, selected: String) {
        map?.clear()
        val selectedMarkerOptions: MarkerOptions? = getMarkOfPlaceName(selected)
        map?.addMarker(selectedMarkerOptions!!)
        Toast.makeText(this@MapsActivity, selected, Toast.LENGTH_SHORT).show()
        // move map camera
        map?.moveCamera(
            CameraUpdateFactory
                .newLatLngZoom(
                    listMarkerOptions[position].position,
                    DEFAULT_ZOOM.toFloat()
                )
        )
    }

    private fun getMarkOfPlaceName(name: String): MarkerOptions? {
        var selected: MarkerOptions? = null
        for (mark in listMarkerOptions) {
            if (mark.title!!.contains(name))
                selected = mark
        }
        return selected
    }

    private fun setupObservers() {
        // listen to api result
        viewModel.nearbyPlacesResponse.observe(
            this,
            EventObserver
                (object : EventObserver.EventUnhandledContent<Resource<NearbySearchResponse>> {
                override fun onEventUnhandledContent(t: Resource<NearbySearchResponse>) {
                    when (t) {
                        is Resource.Loading -> {
                            // show progress bar and remove no data layout while loading
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            // response is ok get the data and display it in the list
                            binding.progressBar.visibility = View.GONE
                            map?.clear()

                            val response = t.response as NearbySearchResponse
                            response.results.forEach {
                                val lat: Double? = it.geometry?.location?.lat
                                val lng: Double? = it.geometry?.location?.lng
                                val placeName: String? = it.name
                                val vicinity: String? = it.vicinity

                                val markerOptions = MarkerOptions()
                                val latLng = LatLng(lat!!, lng!!)
                                markerOptions.position(latLng)
                                markerOptions.title("$placeName : $vicinity")

                                map?.addMarker(markerOptions)
                                markerOptions.icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_RED
                                    )
                                )
                                // move map camera
                                map?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                                map?.animateCamera(CameraUpdateFactory.zoomTo(11F))
                            }

                        }
                        is Resource.DataError -> {
                            // usually this happening when there is server error
                            Toast.makeText(
                                this@MapsActivity,
                                getString(R.string.unknown_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBar.visibility = View.GONE

                        }
                        is Resource.Exception -> {
                            // usually this happening when there is no internet
                            Toast.makeText(
                                this@MapsActivity,
                                getString(R.string.no_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            })
        )
        // listen to api result
        viewModel.placeResponse.observe(
            this,
            EventObserver
                (object : EventObserver.EventUnhandledContent<Resource<NearbySearchResponse>> {
                override fun onEventUnhandledContent(t: Resource<NearbySearchResponse>) {
                    when (t) {
                        is Resource.Loading -> {
                            // show progress bar and remove no data layout while loading
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            // response is ok get the data and display it in the list
                            binding.progressBar.visibility = View.GONE

                            val response = t.response as NearbySearchResponse

                            if (response.results.isNotEmpty()) {
                                val place = response.results[0]
                                binding.txtStatus.text = if (place.openingHours?.openNow == true) {
                                    getString(R.string.open)
                                } else getString(R.string.close)

                                val url = "https://maps.googleapis.com/maps/api/place/photo?photo_reference=" +
                                        "${place.photos[0].photoReference}&maxwidth=200&maxheight=200&key=" +
                                        BuildConfig.MAPS_API_KEY

                                Glide.with(this@MapsActivity).load(url)
                                    .fitCenter()
                                    .into(binding.imgLocation)

                                /*map!!.addPolyline(
                                    PolylineOptions()
                                        .add(LatLng(lastKnownLocation?.latitude!!, lastKnownLocation?.longitude!!),
                                            LatLng(place.geometry?.location?.lat!!, place.geometry?.location?.lng!!))
                                        .width(15f)
                                        .color(Color.GREEN)
                                )*/
                            }

                        }
                        is Resource.DataError -> {
                            // usually this happening when there is server error
                            Toast.makeText(
                                this@MapsActivity,
                                getString(R.string.unknown_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBar.visibility = View.GONE

                        }
                        is Resource.Exception -> {
                            // usually this happening when there is no internet
                            Toast.makeText(
                                this@MapsActivity,
                                getString(R.string.no_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            })
        )
    }

    private fun addMarkersForDummyData() {
        for (markerOption in listMarkerOptions) {
            // Add a marker and move the camera
            map?.addMarker(markerOption)
            map?.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(markerOption.position, DEFAULT_ZOOM.toFloat())
            )
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        this.map = map

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            // Return null here, so that getInfoContents() is called next.
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_info_contents,
                    findViewById<FrameLayout>(R.id.map), false
                )
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.title
                return infoWindow
            }
        })

        map.setOnMarkerClickListener { arg0 ->
            showPlaceDetails(arg0)
            true
        }

        //populate Dummy Data and put markers for them
        setUpDropDownMenu()
        addMarkersForDummyData()

        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        fabMyLocation.setOnClickListener {
            getMyLocation()
        }
        fabNearbyRestaurants.setOnClickListener {
            getNearbyRestaurants()
        }
    }

    private fun showPlaceDetails(marker: Marker) {
        /*// on below line we are creating a new bottom sheet dialog.
        val bottomSheet = BottomSheetDialog(this@MapsActivity)
        // on below line we are creating a new bottom sheet dialog.
        val binding: DialogBottomSheetPlaceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_bottom_sheet_place,
            null,
            false
        )
        binding.txtTitle.text = marker.title
        bottomSheet.setContentView(binding.root)
        bottomSheet.setCancelable(true)
        bottomSheet.show()*/
        binding.txtTitle.text = marker.title
        binding.imgLocation.setImageResource(0)
        viewModel.getPlaceDetailsByTitleAndLocation(marker.title!!, marker.position)
    }

    private fun getMyLocation() {
        val latLng = lastKnownLocation?.let { LatLng(it.latitude, lastKnownLocation!!.longitude) }
        if (latLng != null) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18F)
            map?.animateCamera(cameraUpdate)
        }
    }

    private fun getNearbyRestaurants() {
        if (lastKnownLocation != null) {
            viewModel.getNearbyRestaurants(Const.TYPE_RESTAURANT, lastKnownLocation)
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                        fabMyLocation.isEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                //must be true but here it's custom button
                map?.uiSettings?.isMyLocationButtonEnabled = false
                fabMyLocation.isEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                fabMyLocation.isEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}
