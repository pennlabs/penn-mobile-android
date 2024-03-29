package com.pennapps.labs.pennmobile

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.classes.SublesseeViewModel
import com.pennapps.labs.pennmobile.databinding.FragmentSublesseeDetailsBinding
import com.pennapps.labs.pennmobile.databinding.FragmentSubletteeMarketplaceBinding
import kotlinx.android.synthetic.main.fragment_sublessee_details.interested_sublet_button
import org.joda.time.format.DateTimeFormatter

class SublesseeDetailsFragment (var dataModel: SublesseeViewModel, var position: Int): Fragment() {

    private var _binding : FragmentSublesseeDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mActivity: MainActivity

    private lateinit var saveButton: Button
    private lateinit var interestedButton: Button

    private lateinit var titleText: TextView
    private lateinit var priceText: TextView
    private lateinit var addressText: TextView
    private lateinit var datesText: TextView
    private lateinit var descriptionText: TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity
        mActivity.closeKeyboard()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //container?.removeAllViews()
        // Inflate the layout for this fragment
        _binding = FragmentSublesseeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MutatingSharedPrefs")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var sublet = dataModel.getSublet(position)

        titleText = binding.titleText
        titleText.text = sublet.title

        //NEED IMAGE

        priceText = binding.priceText
        priceText.text = buildString {
            append("$")
            append(sublet.price)
        }

        addressText = binding.addressText
        addressText.text = sublet.address

        datesText = binding.datesText
        datesText.text = buildString {
            append(sublet.startDate)
            append(" - ")
            append(sublet.endDate)
        }

        descriptionText = binding.descriptionText
        descriptionText.text = sublet.description

        //AMENITIES

        saveButton = binding.saveSubletButton
        interestedButton = binding.interestedSubletButton

        saveButton.setOnClickListener {
            saveButton.text = "Saved"
            var savedProperties = sharedPreferences.getStringSet("sublet_saved", HashSet<String>())!!.toSet()
            var newSavedProperties = HashSet<String>()
            newSavedProperties.addAll(savedProperties)
            newSavedProperties.add(sublet.id.toString())
            var mEditor = sharedPreferences.edit()
            mEditor.apply {
                mEditor.putStringSet("sublet_saved", newSavedProperties)
                apply()
            }

            dataModel.addSavedSublet(sublet)
            Toast.makeText(context, "This property has been saved", Toast.LENGTH_LONG).show()
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(((view as ViewGroup).parent as View).id, SublesseeSavedFragment())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }

        interestedButton.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(((view as ViewGroup).parent as View).id, SublesseeInterestForm())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }
}