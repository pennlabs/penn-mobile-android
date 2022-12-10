package com.pennapps.labs.pennmobile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.Course
import com.pennapps.labs.pennmobile.classes.Section
import com.pennapps.labs.pennmobile.viewmodels.PennCourseAlertViewModel
import kotlinx.android.synthetic.main.account_settings_dialog.*
import kotlinx.android.synthetic.main.fragment_penn_course_alert_create_alert.view.*
import kotlinx.android.synthetic.main.fragment_penn_course_alert_manage_alerts.*
import kotlinx.android.synthetic.main.include_main.*
import java.util.regex.Pattern


class PennCourseAlertCreateAlertFragment : Fragment() {
    private val viewModel: PennCourseAlertViewModel by activityViewModels()
    private lateinit var courseSpinner: TextView
    private lateinit var sectionSpinner: TextView
    private lateinit var dialog: Dialog
    private lateinit var mActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_penn_course_alert_create_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as MainActivity


        val sp = PreferenceManager.getDefaultSharedPreferences(activity)

        val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
        viewModel.setBearerTokenValue(bearerToken)

        viewModel.getUserInfo()


        val emailEditText = view.findViewById<EditText>(R.id.pca_email_edit_text)

        val phoneNumberEditText = view.findViewById<EditText>(R.id.pca_phone_edit_text)

        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            val formattedPhoneNumber = viewModel.userInfo.value?.profile?.phone?.drop(2)
            val email = viewModel.userInfo.value?.profile?.email
            phoneNumberEditText.text =
                Editable.Factory.getInstance().newEditable(formattedPhoneNumber)
            emailEditText.text = Editable.Factory.getInstance().newEditable(email)
        })

        val alertButton = view.findViewById<Button>(R.id.pca_alert_button)
        alertButton.isClickable = false

        val notifyClosedCheckbox = view.findViewById<CheckBox>(R.id.pca_notify_checkbox)

        courseSpinner = view.pca_course_spinner
        val courseSpinnerAdapter: ArrayAdapter<Course> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            viewModel.coursesList
        )

        sectionSpinner = view.pca_section_spinner
//        sectionSpinner.isVisible = false
//        sectionSpinner.isClickable = false
        val sectionSpinnerAdapter: ArrayAdapter<Section> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            viewModel.sectionsList
        )

        courseSpinner.setOnClickListener {
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_pca_course_searchable_spinner)
            // set custom height and width
            dialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            // set transparent background
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.window?.setGravity(Gravity.CENTER)

            dialog.setCancelable(true)

            // show dialog
            dialog.show()

            val searchEditText = dialog.findViewById<EditText>(R.id.pca_course_search_edit_text)
            val courseListView = dialog.findViewById<ListView>(R.id.pca_course_list_view)

            courseListView.adapter = courseSpinnerAdapter
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    //only search if course name is >= 3 chars for optimization
                    if (searchEditText.text.length >= 3) {
                        viewModel.getCourses(searchEditText.text.toString(), courseSpinnerAdapter)
                    }
                    courseSpinnerAdapter.notifyDataSetChanged()
                }

                override fun afterTextChanged(s: Editable) {}
            })
            courseListView.onItemClickListener =
                OnItemClickListener { _, _, position, _ -> // when item selected from list
                    // set selected item on textView
                    courseSpinner.text =
                        courseSpinnerAdapter.getItem(position).toString().substringBefore(" -")
                    viewModel.getSections(courseSpinner.text.toString(), sectionSpinnerAdapter)
//                sectionSpinner.isVisible = true
                    // Dismiss dialog
                    dialog.dismiss()
                }
        }


        sectionSpinner.setOnClickListener {
            if (sectionSpinnerAdapter.isEmpty) {
                Toast.makeText(context, "Select course number first!", Toast.LENGTH_SHORT).show()
            } else {
                dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_pca_section_searchable_spinner)
                // set custom height and width
                dialog.window?.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                // set transparent background
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialog.window?.setGravity(Gravity.CENTER)

                dialog.setCancelable(true)

                // show dialog
                dialog.show()

                val searchEditText =
                    dialog.findViewById<EditText>(R.id.pca_section_search_edit_text)
                val sectionListView = dialog.findViewById<ListView>(R.id.pca_section_list_view)

                sectionListView.adapter = sectionSpinnerAdapter
                searchEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        sectionSpinnerAdapter.filter.filter(s)
                    }

                    override fun afterTextChanged(s: Editable) {}
                })
                sectionListView.onItemClickListener =
                    OnItemClickListener { _, _, position, _ -> // when item selected from list
                        // set selected item on textView
                        sectionSpinner.text =
                            sectionSpinnerAdapter.getItem(position).toString().substringBefore(" -")
                        viewModel.selectedSection = sectionSpinnerAdapter.getItem(position)!!
                        viewModel.isSectionSelected = true
                        alertButton.isClickable = true
                        // Dismiss dialog
                        dialog.dismiss()
                    }
            }

        }

        alertButton.setOnClickListener {
//            alertButton.animate().translationYBy(10f).setDuration(100).start()
//            alertButton.animate().translationY(0f)
//            alertButton.animate().translationYBy(-10f).start()

            if (emailEditText.text.isEmpty()) {
                Toast.makeText(
                    context,
                    "Please enter your email address for alert purposes",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (phoneNumberEditText.text.isNotEmpty() && !isValidNumber(phoneNumberEditText.text.toString())) {
                Toast.makeText(
                    context,
                    "Please enter a valid US number (or leave the field empty)",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (viewModel.isSectionSelected) {
                    if (emailEditText.text.toString() != viewModel.userInfo.value?.profile?.email
                        || phoneNumberEditText.text.toString() != viewModel.userInfo.value?.profile?.phone
                    ) {
                        viewModel.updateUserInfo(
                            emailEditText.text.toString(),
                            phoneNumberEditText.text.toString()
                        )
                    }
                    val notifyWhenClosed = notifyClosedCheckbox.isChecked
                    viewModel.createRegistration(
                        viewModel.selectedSection.sectionId,
                        false,
                        notifyWhenClosed
                    )
                    courseSpinner.text = ""
                    sectionSpinner.text = ""
                    courseSpinnerAdapter.clear()
                    sectionSpinnerAdapter.clear()
                    viewModel.clearSelectedSection()
                } else {
                    Toast.makeText(context, "Please select a course section", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.registrationCreatedSuccessfullyToast.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(context, "Registration Created Successfully!", Toast.LENGTH_SHORT)
                    .show()
                viewModel.onSuccessToastDone()
            }
        })

    }


//    override fun onResume() {
//        super.onResume()
//        Log.i("PCA_CF", "Resumed")
////        mActivity.hideBottomBar()
////        mActivity.showBottomBar()
//    }

    private fun isValidNumber(number: String): Boolean {
        if (number.length < 10 || number.length > 13) {
            return false
        }
        val reg = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$"
        var pattern: Pattern = Pattern.compile(reg)
        val matcher = pattern.matcher(number)
        return matcher.matches()
    }


}