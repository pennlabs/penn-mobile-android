package com.pennapps.labs.pennmobile.Subletting

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentSublesseeDetailsMapBinding
import java.util.Locale



class SublesseeDetailsMapFragment(var address: String): Fragment(), OnMapReadyCallback {

    data class LocationInfo(val address: String, val name: String)

    private var _binding : FragmentSublesseeDetailsMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var myMap: GoogleMap
    private lateinit var mActivity: MainActivity
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()

        //Google maps API client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity)

        //Retrieve map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.sublessee_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSublesseeDetailsMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.sublessee_map) as? SupportMapFragment
        val spinner = binding.sublesseeSpinner

        val typesOfBuildings = listOf("Dining", "Libraries")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typesOfBuildings.map { it })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Retrieve selected LocationInfo object
                val selectedLocation = typesOfBuildings[position]

                // Add marker and move camera to selected location
                loadBuildingData(selectedLocation)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }

        mapFragment?.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        //address string to lat/long
        //place marker
        //zoom camera to marker
        // Convert address to LatLng
        val addressCord = getCoordinates(address)
        if (addressCord!=null) {
            // Place marker on map
            myMap.addMarker(MarkerOptions().position(addressCord).title("Sublet Location"))

            // Zoom camera to marker
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressCord, 16.0f))
        }
    }

    fun reset() {
        myMap.clear()
        val addressCord = getCoordinates(address)
        if (addressCord!=null) {
            // Place marker on map
            myMap.addMarker(MarkerOptions().position(addressCord).title("Sublet Location"))
        }
    }

    fun getCoordinates(address : String) : LatLng? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addressList = geocoder.getFromLocationName(address, 1)
        if (!addressList.isNullOrEmpty()) {
            val location = addressList[0]
            return LatLng(location.latitude, location.longitude)
        }
        else {
            val addressListPhilly = geocoder.getFromLocationName(address.plus(" Philadelphia, PA 19104"), 1)
            if (!addressListPhilly.isNullOrEmpty()) {
                val location = addressListPhilly[0]
                return LatLng(location.latitude, location.longitude)
            }
            return null
        }
    }

    fun loadBuildingData(locationCategory : String) {
        reset()


        val diningLocations = listOf(
            LocationInfo("3800 Locust Walk", "1920 Commons"),
            LocationInfo("3465 Sansom Street, English House", "English House"),
            LocationInfo("215 S 39th Street, Falk at Penn Hillel", "Falk at Penn Hillel"),
            LocationInfo("3333 Walnut Street, Hill House", "Hill House"),
            LocationInfo("3335 Woodland Walk, Lauder College House", "Lauder College House"),
            LocationInfo("201 S 40th St, Quaker Kitchen", "Quaker Kitchen"),
            LocationInfo("220 South 33rd Street", "Accenture Café"),
            LocationInfo("201 S 40th St", "Café West"),
            LocationInfo("3800 Locust Walk", "Gourmet Grocer"),
            LocationInfo("3417 Spruce Street","Houston Hall"),
            LocationInfo("3620 Locust Walk", "Joe's Café"),
            LocationInfo("3650 Spruce Street", "McClelland Sushi & Market"),
            LocationInfo("3730 Walnut Street", "Pret a Manger"),
            LocationInfo("3800 Locust Walk", "Starbucks")
        )
        val libraryLocations = listOf(
            LocationInfo("3620 Walnut Street Philadelphia, PA 19104", "Annenberg School Library"),
            LocationInfo("3443 Sansom Street Philadelphia, PA 19104", "Biddle Law Library"),
            LocationInfo("231 South 34th Street Philadelphia, PA 19104", "Chemistry Library"),
            LocationInfo("240 South 40th Street Philadelphia, PA 19104", "Dental Medicine Library"),
            LocationInfo("233 South 33rd Street Philadelphia, PA 19104", "Education Commons"),
            LocationInfo("220 South 34th Street Philadelphia, PA 19104", "Fisher Fine Arts Library"),
            LocationInfo("3610 Hamilton Walk Philadelphia, PA 19104", "Holman Biotech Commons"),
            LocationInfo("209 South 33rd Street Philadelphia, PA 19104","Math/Physics/Astronomy LibraryL"),
            LocationInfo("3260 South Street Philadelphia, PA 19104", "Museum Library"),
            LocationInfo("3420 Walnut Street Philadelphia, PA 19104","Van Pelt")
        )

        val locations = when (locationCategory){
            "Dining" -> diningLocations
            "Libraries" -> libraryLocations
            else -> null
        }

        val icon = when (locationCategory){
            "Dining" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            "Libraries" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            else -> null
        }


        if (locations != null) {
            for (location in locations) {
                val tempCoords = getCoordinates(location.address)
                if(tempCoords!=null) {
                    myMap.addMarker(MarkerOptions().position(tempCoords).title(location.name).icon(icon))
                }
            }
        }
    }
}