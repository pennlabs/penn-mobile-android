package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.annotations.SerializedName
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.AmenitiesItem
import com.pennapps.labs.pennmobile.classes.FitnessPreferenceViewModel
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.classes.SublettingViewModel
import com.pennapps.labs.pennmobile.databinding.FragmentNewListingsBinding
import com.pennapps.labs.pennmobile.databinding.FragmentSubletterPostedListingsBinding

class NewListingsFragment(private val dataModel: SublettingViewModel) : Fragment() {
    private var _binding: FragmentNewListingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mStudentLife: StudentLife

    //Binding
    internal lateinit var titleEt : EditText
    internal lateinit var priceEt : EditText
    internal lateinit var streetAddressEt : EditText
    internal lateinit var apartmentEt : EditText
    internal lateinit var zipCodeEt : EditText
    internal lateinit var startEt : EditText
    internal lateinit var endEt : EditText
    internal lateinit var bedsSpinner : Spinner
    internal lateinit var bathsSpinner : Spinner
    internal lateinit var descriptionEt : EditText
    internal lateinit var bathroomCheck : CheckBox
    internal lateinit var laundryCheck : CheckBox
    internal lateinit var gymCheck : CheckBox
    internal lateinit var wifiCheck : CheckBox
    internal lateinit var furnishedCheck : CheckBox
    internal lateinit var closetCheck : CheckBox
    internal lateinit var utilitiesCheck : CheckBox
    internal lateinit var poolCheck : CheckBox
    internal lateinit var loungeCheck : CheckBox
    internal lateinit var parkingCheck : CheckBox
    internal lateinit var patioCheck : CheckBox
    internal lateinit var kitchenCheck : CheckBox
    internal lateinit var dogCheck : CheckBox
    internal lateinit var catCheck : CheckBox








    //Sublet variables
    private lateinit var title : String
    private lateinit var price : String
    private var streetAddress : String? = null
    private var apartment : String? = null
    private var zipCode : String? = null
    private lateinit var startDate : String
    private lateinit var endDate : String
    private var beds : Int? = null
    private var baths : Int? = null
    private var description: String?  = null
    private lateinit var amenities: List<AmenitiesItem>

    private lateinit var mActivity: MainActivity



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.hideBottomBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNewListingsBinding.inflate(inflater, container, false)
        val view = binding.root
        super.onViewCreated(view, savedInstanceState)

        titleEt = binding.listingNameInput
        priceEt = binding.priceInput
        streetAddressEt = binding.streetAddressInput
        apartmentEt = binding.apartmentInput
        zipCodeEt = binding.postalCodeInput
        startEt = binding.startDateInput
        endEt = binding.endDateInput
        bedsSpinner = binding.bedInput
        bathsSpinner = binding.bathInput
        descriptionEt = binding.descriptionInput
        bathroomCheck = binding.bathroomCheck
        laundryCheck = binding.laundryCheck
        gymCheck = binding.gymCheck
        wifiCheck = binding.wifiCheck
        furnishedCheck = binding.furnishedCheck
        closetCheck = binding.closetCheck
        utilitiesCheck = binding.utilitiesCheck
        poolCheck = binding.poolCheck
        loungeCheck = binding.loungeCheck
        parkingCheck = binding.parkingCheck
        patioCheck = binding.patioCheck
        kitchenCheck = binding.kitchenCheck
        dogCheck = binding.dogCheck
        catCheck = binding.catCheck



        binding.postButton.setOnClickListener{
            if (titleEt.text.toString().matches("".toRegex())
                    || priceEt.text.toString().matches("".toRegex())
                    || startEt.text.toString().matches("".toRegex())
                    || endEt.text.toString().matches("".toRegex())) {
                Toast.makeText(activity, "Please fill in all required fields before booking",
                        Toast.LENGTH_LONG).show()
            } else {
                title = titleEt.text.toString()
                price = priceEt.text.toString()
                streetAddress = streetAddressEt.text.toString() + ", " + apartmentEt.text.toString() +
                        ", " + zipCodeEt.text.toString()
                if (streetAddress.equals(", , ")) {
                    streetAddress = null
                }
                startDate = startEt.text.toString()
                endDate = endEt.text.toString()

                beds = bedsSpinner.selectedItemPosition + 1
                baths = bathsSpinner.selectedItemPosition + 1

                val amenitiesList = mutableListOf<String>()
                if(bathroomCheck.isChecked) { amenitiesList.add("Private Bathroom") }
                if(laundryCheck.isChecked) { amenitiesList.add("In-Unit Laundry") }
                if(gymCheck.isChecked) { amenitiesList.add("Gym") }
                if(wifiCheck.isChecked) { amenitiesList.add("Wifi") }
                if(furnishedCheck.isChecked) { amenitiesList.add("Furnished") }
                if(closetCheck.isChecked) { amenitiesList.add("Walk-in Closet") }
                if(utilitiesCheck.isChecked) { amenitiesList.add("Utilities Included") }
                if(poolCheck.isChecked) { amenitiesList.add("Swimming Pool") }
                if(loungeCheck.isChecked) { amenitiesList.add("Resident Lounge") }
                if(parkingCheck.isChecked) { amenitiesList.add("Parking") }
                if(patioCheck.isChecked) { amenitiesList.add("Patio") }
                if(kitchenCheck.isChecked) { amenitiesList.add("Kitchen") }
                if(dogCheck.isChecked) { amenitiesList.add("Dog-Friendly") }
                if(catCheck.isChecked) { amenitiesList.add("Cat-Friendly") }

                description = descriptionEt.text.toString()
                if (description.equals("")) {
                    description = null;
                }
                postSublet(title, Integer.parseInt(price), streetAddress, startDate, endDate, beds,
                        baths, amenitiesList, description)

            }

        }
        return view
    }

    private fun postSublet(title : String, price : Int, address : String?, startDate: String,
                           endDate : String, beds: Int?, baths: Int?, amenities: List<String>,
                           description: String?) {

        val newSublet = Sublet(
                endDate = endDate,
                amenities = amenities,
                baths = baths,
                address = address,
                maxPrice = price,//fix
                expiresAt = " ",
                minPrice = 0, // fix
                description = description,
                title = title,
                beds = beds,
                externalLink = "test.com", // fix
                startDate = startDate
        )

        dataModel.postSublet(mActivity, newSublet)
    }


}