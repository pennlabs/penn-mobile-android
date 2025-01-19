package com.pennapps.labs.pennmobile


import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.pennapps.labs.pennmobile.Subletting.SubletteeMarketplace
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.FragmentSubletteeViewBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SubletteeFragment : Fragment() {


    // Create binding
    private var _binding: FragmentSubletteeViewBinding? = null
    private val binding get() = _binding!!


    // API manager
    private lateinit var mStudentLife: StudentLife


    private lateinit var mActivity: MainActivity
    private lateinit var placesClient: PlacesClient
    private val sessionToken = AutocompleteSessionToken.newInstance()


    // Calendar related variables
    private lateinit var btnStartDate: Button
    private lateinit var btnEndDate: Button
    private var startCalendar: Calendar = Calendar.getInstance()
    private var endCalendar: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    // The "Flexible" check boxes
    private lateinit var btnDatesFlexible: Button
    private lateinit var btnLocationFlexible: Button
    private var areDatesFlexible: Boolean = false
    private var areLocationsFlexible: Boolean = false

    // Address adapter for autocomplete
    private lateinit var addressAdapter: ArrayAdapter<String>


    // Default addresses to display when input is empty
    private val defaultAddresses = listOf(
        "3600 Chestnut St, Philadelphia, PA",
        "3700 Walnut St, Philadelphia, PA",
        "3800 Spruce St, Philadelphia, PA"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()


        val applicationInfo = requireContext().packageManager
            .getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val apiKey = applicationInfo.metaData.getString("com.google.android.geo.API_KEY")
            ?: throw IllegalStateException("API key not found in AndroidManifest.xml")


        // Initialize Places SDK with API key from manifest
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }
        placesClient = Places.createClient(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSubletteeViewBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Initialize address adapter with default addresses
        addressAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, defaultAddresses)
        binding.subletteeLocationEdittext.setAdapter(addressAdapter)
        binding.subletteeLocationEdittext.threshold = 0


        setupAddressAutocomplete()
        setupDatePickers()
        setupFlexibleBoxes()


        val marketplaceButton: Button = view.findViewById(R.id.sublettee_enter_subletting_button)


        marketplaceButton.setOnClickListener {
            if (!validateDates()) {
                Toast.makeText(context, "Please select valid start and end dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!validatePrices()) {
                Toast.makeText(context, "Please enter a valid price range", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Load new fragment, which will hold the subletting marketplace in whole
            mActivity.supportFragmentManager.beginTransaction()
                .replace(((view as ViewGroup).parent as View).id, SubletteeMarketplace())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }


        // Handle selection from dropdown
        binding.subletteeLocationEdittext.setOnItemClickListener { parent, _, position, _ ->
            val selectedAddress = parent.getItemAtPosition(position) as String
            // Update the address text view with the selected address
            binding.subletteeLocationAddressView.text = selectedAddress
        }
    }

    private fun validatePrices(): Boolean {
        val minPriceString = binding.subletteeMinPriceText.text.toString().trim()
        val maxPriceString = binding.subletteeMaxPriceText.text.toString().trim()

        if (minPriceString.isEmpty() || maxPriceString.isEmpty()) {
            return false
        }

        val minPrice = minPriceString.toDoubleOrNull()
        val maxPrice = maxPriceString.toDoubleOrNull()

        if (minPrice == null || maxPrice == null || minPrice < 0 || maxPrice < 0) {
            return false
        }

        if (minPrice > maxPrice) {
            return false
        }

        return true
    }

    private fun setupFlexibleBoxes() {
        //Dates Flexible
        btnDatesFlexible = binding.subletteeDateCheckbox
        btnDatesFlexible.setOnClickListener {
            areDatesFlexible = !areDatesFlexible
        }
        //Location Flexible
        btnLocationFlexible = binding.subletteeLocationCheckbox
        btnLocationFlexible.setOnClickListener {
            areLocationsFlexible = !areLocationsFlexible
        }
    }


    private fun setupDatePickers() {
        // Start Date
        btnStartDate = binding.subletteeStartDateButton
        btnStartDate.setOnClickListener {
            showDatePicker(btnStartDate, startCalendar)
        }
        btnEndDate = binding.subletteeEndDateButton
        btnEndDate.setOnClickListener {
            showDatePicker(btnEndDate, endCalendar)
        }
    }


    private fun showDatePicker(btn: Button, cal: Calendar) {
        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    // Update calendar instance
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    // Update Button
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    btn.text = formattedDate
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
            )
        }
        datePickerDialog?.show()
    }


    private fun validateDates(): Boolean {
        val currentDate = Calendar.getInstance()
        return !(startCalendar < currentDate || startCalendar > endCalendar) || areDatesFlexible
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun setupAddressAutocomplete() {
        binding.subletteeLocationEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}


            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getAddressPredictions(query.toString())
                if (binding.subletteeLocationEdittext.hasFocus()) {
                    binding.subletteeLocationEdittext.showDropDown()
                }
            }


            override fun afterTextChanged(p0: Editable?) {}
        })


        // Show the dropdown when the view gains focus and update when it loses focus
        binding.subletteeLocationEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.subletteeLocationEdittext.showDropDown()
            } else {
                // When the view loses focus, update the address text view
                updateAddressTextView()
            }
        }
    }


    private fun updateAddressTextView() {
        val query = binding.subletteeLocationEdittext.text.toString()
        if (query.isNotEmpty()) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()


            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val predictions = response.autocompletePredictions
                    if (predictions.isNotEmpty()) {
                        val bestPrediction = predictions[0].getFullText(null).toString()
                        binding.subletteeLocationAddressView.text = "Address: $bestPrediction"
                    } else {
                        // No predictions found; set to original query
                        binding.subletteeLocationAddressView.text = "Address: $query"
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        Log.e("Places", "Place not found: ${exception.message}")
                    }
                    // On failure, set to original query
                    binding.subletteeLocationAddressView.text = "Address: $query"
                }
        } else {
            // Clear the address text view if the query is empty
            binding.subletteeLocationAddressView.text = "Address: "
        }
    }


    private fun getAddressPredictions(query: String) {
        if (query.isEmpty()) {
            // Show default addresses
            addressAdapter.clear()
            addressAdapter.addAll(defaultAddresses)
            addressAdapter.notifyDataSetChanged()
        } else {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()


            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val predictions = response.autocompletePredictions
                    val addresses = predictions.take(3).map { it.getFullText(null).toString() }
                    addressAdapter.clear()
                    addressAdapter.addAll(addresses)
                    addressAdapter.notifyDataSetChanged()
                    // Show the dropdown if the text box is focused
                    if (binding.subletteeLocationEdittext.hasFocus()) {
                        //binding.subletteeLocationEdittext.showDropDown()
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        Log.e("Places", "Place not found: ${exception.message}")
                    }
                }
        }
    }
}
