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
import com.pennapps.labs.pennmobile.databinding.FragmentNewListingsBinding
import com.pennapps.labs.pennmobile.databinding.FragmentSubletterPostedListingsBinding

class NewListingsFragment : Fragment() {
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
    private lateinit var price : EditText
    private var streetAddress : String? = null
    private var apartment : String? = null
    private var zipCode : String? = null
    private lateinit var startDate : EditText
    private lateinit var endDate : EditText
    private var beds : Int? = null
    private var baths : Int? = null
    private var description: String?  = null


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
            }

        }
        return view
    }

    private fun postSublet(title : String, price : Int, address : String?, startDate: String,
                           endDate : String, beds: Int?, baths: Int?, amenities: List<AmenitiesItem>,
                           description: String?) {

    }


}