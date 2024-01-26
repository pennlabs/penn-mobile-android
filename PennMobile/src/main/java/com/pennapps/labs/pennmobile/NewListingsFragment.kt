package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    internal lateinit var bedsEt : Spinner
    internal lateinit var bathsEt : Spinner
    internal lateinit var descriptionEt : EditText

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
        val mActivity : MainActivity = activity as MainActivity
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
        bedsEt = binding.bedInput
        bathsEt = binding.bathInput
        descriptionEt = binding.descriptionInput

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
                //do beds, baths
                description = descriptionEt.text.toString()
                if (description.equals("")) {
                    description = null;
                }
                postSublet(title, Integer.parseInt(price), streetAddress, startDate, endDate, beds,
                        baths, amenities, description)

            }

        }
        return view
    }

    private fun postSublet(title : String, price : Int, address : String?, startDate: String,
                           endDate : String, beds: Int?, baths: Int?, amenities: List<AmenitiesItem>,
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
                externalLink = " ", // fix
                startDate = startDate
        )

        dataModel.postSublet(mActivity, newSublet)
    }


}