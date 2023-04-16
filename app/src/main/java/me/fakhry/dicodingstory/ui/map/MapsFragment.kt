package me.fakhry.dicodingstory.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import me.fakhry.dicodingstory.R
import me.fakhry.dicodingstory.databinding.FragmentMapsBinding
import me.fakhry.dicodingstory.network.model.StoryWithLoc
import me.fakhry.dicodingstory.repository.Result
import me.fakhry.dicodingstory.ui.createstory.CreateStoryFragmentArgs

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding
    private val args: CreateStoryFragmentArgs by navArgs()
    private val factory: MapsViewModelFactory = MapsViewModelFactory.getInstance()
    private val mapsViewModel: MapsViewModel by viewModels { factory }

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isIndoorLevelPickerEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle(googleMap)
        addManyMarker(googleMap)
    }

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding?.root
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
        viewLifecycleOwner.lifecycleScope.launch {
            mapsViewModel.getAllStoriesWithLocation(token)
        }
        mapsViewModel.getAllStoriesWithLocation("Bearer $token")
            .observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> binding?.progressBar?.visibility = View.VISIBLE
                        is Result.Success -> {
                            binding?.progressBar?.visibility = View.GONE
                            populateMap(result, googleMap)
                        }
                        is Result.Error -> {
                            binding?.progressBar?.visibility = View.GONE
                            activity?.let { activity ->
                                Snackbar.make(
                                    activity.findViewById(R.id.map),
                                    result.error,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
    }

    private fun populateMap(result: Result.Success<List<StoryWithLoc>>, googleMap: GoogleMap) {
        result.data.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.description)
            )
            boundsBuilder.include(latLng)
        }
        val bounds: LatLngBounds = boundsBuilder.build()
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                0
            )
        )
    }

    companion object {
        private const val TAG = "MapsFragment"
    }
}