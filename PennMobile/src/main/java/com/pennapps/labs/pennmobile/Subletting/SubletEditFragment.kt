package com.pennapps.labs.pennmobile.Subletting

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.AmenitiesItem
import com.pennapps.labs.pennmobile.classes.MultipartUtil
import com.pennapps.labs.pennmobile.databinding.FragmentSubletterEditListingBinding
import okhttp3.MultipartBody
import java.io.IOException

class SubletEditFragment (private val dataModel: SublettingViewModel, private val subletNumber: Int) : Fragment() {
    private var _binding: FragmentSubletterEditListingBinding? = null
    private val binding get() = _binding!!

    private lateinit var mStudentLife: StudentLife

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
    internal lateinit var imageView: ImageView
    internal lateinit var imageIcon: ImageView
    internal lateinit var imageText: TextView

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

    private var image: String? = null
    var multipartImage: MultipartBody.Part? = null

    private lateinit var mActivity: MainActivity



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.hideBottomBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentSubletterEditListingBinding.inflate(inflater, container, false)
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
        imageView = binding.mainImage
        imageIcon = binding.mainImageIcon
        imageText = binding.addPhotosText

        val sublet : Sublet = dataModel.getSublet(subletNumber)
        titleEt.setText(sublet.title)
        priceEt.setText(sublet.price.toString())

        if (sublet.address != null) {
            val addressComponents = sublet.address.split(", ")
            streetAddressEt.setText(addressComponents.getOrElse(0) { "" })
            apartmentEt.setText(addressComponents.getOrElse(1) { "" })
            zipCodeEt.setText(addressComponents.getOrElse(2) { "" })
        }

        startEt.setText(sublet.startDate)
        endEt.setText(sublet.endDate)
        descriptionEt.setText(sublet.description)


        context?.let {
            Glide.with(it)
                .load(sublet.images?.get(0)?.imageUrl)
                .centerCrop() // optional - adjust as needed
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.mainImage)
        }

        val amenitiesList = sublet.amenities ?: emptyList()

        for (amenity in amenitiesList) {
            when (amenity) {
                "Private Bathroom" -> bathroomCheck.isChecked = true
                "In-unit Laundry" -> laundryCheck.isChecked = true
                "In-Unit Laundry" -> laundryCheck.isChecked = true
                "Gym" -> gymCheck.isChecked = true
                "Wifi" -> wifiCheck.isChecked = true
                "Furnished" -> furnishedCheck.isChecked = true
                "Walk-in Closet" -> closetCheck.isChecked = true
                "Utilities Included" -> utilitiesCheck.isChecked = true
                "Swimming Pool" -> poolCheck.isChecked = true
                "Resident Lounge" -> loungeCheck.isChecked = true
                "Parking" -> parkingCheck.isChecked = true
                "Patio" -> patioCheck.isChecked = true
                "Kitchen" -> kitchenCheck.isChecked = true
                "Dog-Friendly" -> dogCheck.isChecked = true
                "Cat-Friendly" -> catCheck.isChecked = true
            }
        }


        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                try {
                    // Load the selected image into the ImageView
                    val inputStream = context?.contentResolver?.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    image = bitmap.toString()
                    imageView.setImageBitmap(bitmap)
                    imageIcon.visibility = View.GONE
                    imageText.visibility = View.GONE
                    multipartImage = MultipartUtil.createPartFromBitmap(bitmap)



                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        imageView.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        val dateRegex = Regex("""^(0[1-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1])/\d{2}$""")

        binding.postButton.setOnClickListener{
            if (titleEt.text.toString().matches("".toRegex())
                || priceEt.text.toString().matches("".toRegex())
                || startEt.text.toString().matches("".toRegex())
                || endEt.text.toString().matches("".toRegex())) {
                Toast.makeText(activity, "Please fill in all required fields",
                    Toast.LENGTH_LONG).show()
            } else if (!startEt.text.toString().matches(dateRegex) || !endEt.text.toString().matches(dateRegex)) {
                Toast.makeText(activity, "Please follow formatting instructions for date",
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

                editSublet(title, Integer.parseInt(price), streetAddress, startDate, endDate, beds,
                    baths, amenitiesList, description)

            }

        }
        return view
    }

    private fun editSublet(title : String, price : Int, address : String?, startDate: String,
                           endDate : String, beds: Int?, baths: Int?, amenities: List<String>?,
                           description: String?) {
        val convertedEnd = convertToYYYYMMDD(endDate)
        val convertedStart = convertToYYYYMMDD(startDate)
        var subletId = -1


        val newSublet = Sublet(
            endDate = convertedEnd,
            baths = baths,
            address = address,
            price = price,//fix
            expiresAt = "3000-02-01T10:48:02-05:00", //?
            description = description,
            title = title,
            beds = beds,
            amenities = amenities,
            externalLink = "https://pennlabs.org/", // fix
            startDate = convertedStart
        )

        dataModel.editSublet(mActivity, newSublet, subletNumber) { postedSublet ->
            if (postedSublet != null) {
                Log.i("MainActivity", "Posted sublet ID: ${postedSublet.id}")
                subletId = postedSublet.id!!
            } else {
                // Handle failure to post sublet
                Log.e("MainActivity", "Failed to post sublet")
            }
        }

        if (multipartImage != null) {
            val subletPart = MultipartUtil.createSubletPart(subletId)
            dataModel.postImage(mActivity, subletId,  subletPart, multipartImage!!)
        }





    }

    private fun convertToYYYYMMDD(mmddyy: String): String {
        val components = mmddyy.split("/")
        val month = components[0].toInt()
        val day = components[1].toInt()
        val year = components[2].toInt() + 2000

        return String.format("%04d-%02d-%02d", year, month, day)
    }




}