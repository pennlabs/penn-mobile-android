package com.pennapps.labs.pennmobile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.Course
import com.pennapps.labs.pennmobile.classes.Section
import com.pennapps.labs.pennmobile.viewmodels.PennCourseAlertViewModel
import kotlinx.android.synthetic.main.fragment_penn_course_alert_create_alert.view.*


class PennCourseAlertCreateAlertFragment : Fragment() {
    private val viewModel: PennCourseAlertViewModel by activityViewModels()
    private lateinit var courseSpinner: TextView
    private lateinit var sectionSpinner: TextView
    private lateinit var dialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_penn_course_alert_create_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val email = sp.getString(getString(R.string.email_address), "")

        val emailEditText = view.findViewById<EditText>(R.id.pca_email_edit_text)
        emailEditText.text = Editable.Factory.getInstance().newEditable(email)

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
        sectionSpinner.isVisible = false
        val sectionSpinnerAdapter: ArrayAdapter<Section> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            viewModel.sectionsList
        )



        courseSpinner.setOnClickListener{
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_pca_course_searchable_spinner);
            // set custom height and width
            dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            // set transparent background
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            dialog.window?.setGravity(Gravity.CENTER)

            dialog.setCancelable(true)

            // show dialog
            dialog.show();

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
            courseListView.onItemClickListener = OnItemClickListener { _, _, position, _ -> // when item selected from list
                // set selected item on textView
                courseSpinner.text = courseSpinnerAdapter.getItem(position).toString()
                viewModel.getSections(courseSpinner.text.toString(), sectionSpinnerAdapter)
                sectionSpinner.isVisible = true
                // Dismiss dialog
                dialog.dismiss()
            }
        }


        sectionSpinner.setOnClickListener{
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_pca_section_searchable_spinner);
            // set custom height and width
            dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            // set transparent background
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            dialog.window?.setGravity(Gravity.CENTER)

            dialog.setCancelable(true)

            // show dialog
            dialog.show();

            val searchEditText = dialog.findViewById<EditText>(R.id.pca_section_search_edit_text)
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

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    sectionSpinnerAdapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })
            sectionListView.onItemClickListener = OnItemClickListener { _, _, position, _ -> // when item selected from list
                // set selected item on textView
                sectionSpinner.text = sectionSpinnerAdapter.getItem(position).toString()
                viewModel.selectedSection = sectionSpinnerAdapter.getItem(position)!!
                viewModel.isSectionSelected = true
                alertButton.isClickable = true
                // Dismiss dialog
                dialog.dismiss()
            }
        }

        alertButton.setOnClickListener {
            if (viewModel.isSectionSelected) {
                val notifyWhenClosed = notifyClosedCheckbox.isChecked
                viewModel.createRegistration(viewModel.selectedSection.sectionId, false, notifyWhenClosed)
            }
        }

    }


}