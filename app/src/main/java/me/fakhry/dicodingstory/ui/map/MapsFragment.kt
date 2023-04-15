package me.fakhry.dicodingstory.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.ui.createstory.CreateStoryFragmentArgs

class MapsFragment : Fragment() {

    private val viewModel: MapsViewModel by viewModels()
    private val args: CreateStoryFragmentArgs by navArgs()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isIndoorLevelPickerEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//
//        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(dicodingSpace)
//                .title("Dicoding Space")
//                .snippet("Batik Kumeli No.50")
//        )
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

        setMapStyle(googleMap)
        addManyMarker(googleMap)
    }

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setMapStyle(googleMap: GoogleMap) {
        try {
            val success =
                googleMap.setMapStyle(context?.let {
                    MapStyleOptions.loadRawResourceStyle(
                        it,
                        R.raw.map_style
                    )
                })
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun addManyMarker(googleMap: GoogleMap) {
        val token = args.token
        viewLifecycleOwner.lifecycle
//        viewLifecycleOwner.lifecycleScope.launch {
        viewModel.getAllStoriesWithLocation(token)
//        }
        viewModel.storyList.observe(viewLifecycleOwner) { storyList ->
            storyList.forEach { story ->
                Log.d(TAG, "${story.lat} & ${story.lon}")
                val latLng = LatLng(story.lat, story.lon)
                googleMap.addMarker(MarkerOptions().position(latLng).title(story.description))
                boundsBuilder.include(latLng)
            }
            val bounds: LatLngBounds = boundsBuilder.build()
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }

//        val tourismPlace = listOf(
//            TourismPlace("Floating Market Lembang", -6.8168954, 107.6151046),
//            TourismPlace("The Great Asia Africa", -6.8331128, 107.6048483),
//            TourismPlace("Rabbit Town", -6.8668408, 107.608081),
//            TourismPlace("Alun-Alun Kota Bandung", -6.9218518, 107.6025294),
//            TourismPlace("Orchid Forest Cikole", -6.780725, 107.637409),
//        )
//        tourismPlace.forEach { tourism ->
//            val latLng = LatLng(tourism.latitude, tourism.longitude)
//            googleMap.addMarker(MarkerOptions().position(latLng).title(tourism.name))
//            boundsBuilder.include(latLng)
//        }

//        userSharedViewModel.storyList.observe(viewLifecycleOwner) { stories ->
//            Log.d(TAG, stories.toString())
//            stories.filter {
//                it.lat != null && it.lon != null
//            }.forEach { story ->
//                Log.d(TAG, story.description)
//                if (story.lat != null && story.lon != null) {
//                    val latLng = LatLng(story.lat, story.lon)
//                    val name = story.name
//                    googleMap.addMarker(MarkerOptions().position(latLng).title(name))
//                    boundsBuilder.include(latLng)
//                }
//            }
//            val bounds: LatLngBounds = boundsBuilder.build()
//            googleMap.animateCamera(
//                CameraUpdateFactory.newLatLngBounds(
//                    bounds,
//                    resources.displayMetrics.widthPixels,
//                    resources.displayMetrics.heightPixels,
//                    300
//                )
//            )
//        }


    }

    companion object {
        private const val TAG = "MapsFragment"
    }
}