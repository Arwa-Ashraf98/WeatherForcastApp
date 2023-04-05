package com.example.weatherforecastapp.ui.maps.view

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.databinding.FragmentMapBinding
import com.example.weatherforecastapp.ui.maps.utils.MapManager
import com.example.weatherforecastapp.ui.maps.utils.MapsManagerInterface
import com.example.weatherforecastapp.ui.maps.viewModel.MapViewModel
import com.example.weatherforecastapp.ui.maps.viewModel.MapViewModelFactory
import com.example.weatherforecastapp.utils.Constants
import com.example.weatherforecastapp.utils.GeoCoderConverter
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, MapsManagerInterface {

    private lateinit var googleMap: GoogleMap
    private var binding: FragmentMapBinding? = null
    private lateinit var mapManager: MapManager
    private lateinit var mapViewModel: MapViewModel
    private val TAG: String = "TAG"
    private lateinit var addressToSave: String
//    private late init var autoComplete: AutocompleteSupportFragment
    private var comeFrom: String = "settings"
    private lateinit var favAddress: FavAddress
    private var lat: Double = 0.0
    private var long: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMap()
//        setUpAutoComplete()
        mapManager = MapManager(requireContext(), this)
        comeFrom = MapFragmentArgs.fromBundle(requireArguments()).destination
        initViewModel()
    }

    private fun initViewModel() {
        val mapViewModelFactory = MapViewModelFactory(
            Repo.getRepoInstance(
                RemoteSource.getRemoteSourceInstance(),
                LocalSource.getLocalSourceInstance()
            )
        )
        mapViewModel =
            ViewModelProvider(requireActivity(), mapViewModelFactory)[MapViewModel::class.java]

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        onClicks()
        init()


    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun init() {
        binding?.search?.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || keyEvent?.action == KeyEvent.ACTION_DOWN
                || keyEvent?.action == KeyEvent.KEYCODE_ENTER
            ) {
                geoLocate()
            }
            false
        }
        hideSoftKeyBoard()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun onClicks() {
        binding?.apply {
            gpsImageView.setOnClickListener {
                mapManager.getLastLocation()
            }

            googleMap.setOnMapClickListener {
                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions().position(it).title(
                        GeoCoderConverter.getAddress(
                            requireContext(),
                            it.latitude,
                            it.longitude
                        )
                    )
                )

                createAlertDialog(it)

            }
        }
    }


    private fun geoLocate() {
        val searchString: String = binding?.search?.text.toString()
        val geocoder = Geocoder(requireContext())
        var list: List<Address>? = ArrayList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)
        } catch (e: IOException) {
            Log.e(TAG, "geoLocate: IOException: " + e.message)
        }

        if (list!!.isNotEmpty()) {
            val address = list[0]
            val latLng = LatLng(address.latitude, address.longitude)
            addressToSave =
                GeoCoderConverter.getAddress(
                    requireContext(),
                    latLng.latitude,
                    latLng.longitude
                ) as String
            animateCamera(latLng)
            addMarker(latLng, addressToSave)
            hideSoftKeyBoard()
        }
    }

    private fun animateCamera(city: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(city, 15f))

    }

    private fun addMarker(latLng: LatLng, title: String) {
        googleMap.addMarker(
            MarkerOptions().position(latLng).title(title)
        )
    }

    private fun hideSoftKeyBoard() {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    private fun setLocationOnMap(lat: Double?, long: Double?, title: String = "My Location") {
        val city = LatLng(lat!!, long!!)
        addMarker(city, title)
        animateCamera(city)
    }

    private fun saveAddress(favAddress: FavAddress) {
        mapViewModel.insertFavLocation(favAddress)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun createAlertDialog(it: LatLng) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        alert.setTitle("SAVE")
        alert.setMessage("Do You want to save this Place to your ${comeFrom.uppercase()}?")
        alert.setPositiveButton("Save") { _: DialogInterface, _: Int ->
            if (comeFrom.equals("fav", true)) {
                // save to database
                actionAtFavourite(it)

            } else if (comeFrom.equals("settings", true)) {
                // save to shared preferences
                actionAtSettings(it)
            } else {
                // he comes from home home to choose specific location so
                // hw will save it to shred preference and
                // save that he choose maps
                // or he can come from alerts
//                mapManager.getLastLocation()
//                MySharedPreferences.setLatitude(it.latitude)
//                MySharedPreferences.setLongitude(it.longitude)
//                MySharedPreferences.setLocation(Constants.MAP_LOCATION_VALUES)
//                startActivity(Intent(requireContext(), MainActivity::class.java))
            }

        }
        alert.setNegativeButton("No") { _: DialogInterface, _: Int ->
            Toast.makeText(
                requireContext(),
                "Saving Process is Canceled",
                Toast.LENGTH_SHORT
            ).show()
        }

        val dialog = alert.create()
        dialog.show()
    }

    private fun actionAtFavourite(it: LatLng) {
        favAddress = FavAddress(
            it.latitude,
            it.longitude,
            addressToSave,
            it.latitude.toString().plus(it.longitude.toString())
        )
        saveAddress(favAddress)
        Toast.makeText(
            requireContext(),
            "Data has been Saved Successfully",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun actionAtSettings(it: LatLng) {
        MySharedPreferences.setLatitude(it.latitude)
        MySharedPreferences.setLongitude(it.longitude)
        Log.d(TAG, "createAlertDialog: " + it.longitude.toString())
        Log.d(TAG, "createAlertDialog: " + it.latitude.toString())
        MySharedPreferences.setLocation(Constants.MAP_LOCATION_VALUES)
        activity?.fragmentManager?.popBackStack()
    }

    override fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.PERMISSION_LOCATION_ID_MAP_FRAGMENT
        )
    }


    override fun getLocationResult(locationResult: LocationResult?) {
        Log.d(TAG, "getLocationResult: FROM MAP")
        locationResult?.let {
            addressToSave = GeoCoderConverter.getAddress(
                requireContext(),
                it.lastLocation?.latitude as Double,
                it.lastLocation?.longitude as Double
            ) as String
            lat = it.lastLocation?.latitude as Double
            long = it.lastLocation?.longitude as Double
            setLocationOnMap(lat, long)
            Log.d(
                "TAG",
                "getLocationResult: INSIDE INSIDE AND HE DID NOT SAVE LOCATION JUST ANIMATE"
            )
        } ?: throw NullPointerException("NULl")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}


/*
    private fun setUpAutoComplete() {
        Log.e(TAG, "setUpAutoComplete: ")
        Places.initialize(requireContext(), Constants.MAP_API_KEY)
        autoComplete =
            childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment
        autoComplete.setTypeFilter(TypeFilter.CITIES)
        autoComplete.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.PHOTO_META DATA S
            )
        )
        autoComplete.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }

            override fun onPlaceSelected(place: Place) {
                place.latLng.let {
                    setLocationOnMap(it?.latitude, it?.longitude, title = place.name as String)
                }
//                if (mapId.equals("fav", true)) {
//                    // insert location to favourite
//
//                } else {
//                    // u come from settings u will use shared preference to save lat and long
//
//                }


                val lat = place.latLng?.latitude
                val long = place.latLng?.longitude
                Log.i(TAG, "$lat /////////// $long")
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
            }

        })

    }

 */