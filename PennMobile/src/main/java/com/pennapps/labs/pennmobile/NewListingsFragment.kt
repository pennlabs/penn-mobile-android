package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentNewListingsBinding.inflate(inflater, container, false)
        binding.draftButton.setOnClickListener{
            Toast.makeText(requireContext(), "button clicked", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    private fun postSublet(title : String, price : Int, address : String?, startDate: String,
                           endDate : String, beds: Int?, baths: Int?, amenities: List<AmenitiesItem>,
                           description: String?) {

    }

    /*
    data class Sublet(@SerializedName("end_date")
                      val endDate: String = "",
                      @SerializedName("amenities")
                      val amenities: List<AmenitiesItem>??,
                      @SerializedName("baths")
                      val baths: Int? = 0,
                      @SerializedName("address")
                      val address: String? = "",
                      @SerializedName("max_price")
                      val maxPrice: Int = 0,
                      @SerializedName("expires_at")
                      val expiresAt: String = "",
                      @SerializedName("min_price")
                      val minPrice: Int = 0,
                      @SerializedName("description")
                      val description: String? = "",
                      @SerializedName("title")
                      val title: String = "",
                      @SerializedName("beds")
                      val beds: Int? = 0,
                      @SerializedName("external_link")
                      val externalLink: String = "",
                      @SerializedName("start_date")
                      val startDate: String = "")

     */
}