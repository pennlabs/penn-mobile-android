package com.pennapps.labs.pennmobile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pennapps.labs.pennmobile.classes.Course
import com.pennapps.labs.pennmobile.viewmodels.PennCourseAlertViewModel
import kotlinx.android.synthetic.main.fragment_penn_course_alert_create_alert.view.*


class PennCourseAlertCreateAlertFragment : Fragment() {
    private val viewModel: PennCourseAlertViewModel by activityViewModels()
    private lateinit var courseSpinner: TextView
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
        courseSpinner = view.pca_course_spinner
        courseSpinner.setOnClickListener{
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_searchable_spinner);
            // set custom height and width
            dialog.window?.setLayout(650,800);
            // set transparent background
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            // show dialog
            dialog.show();

            val searchEditText = dialog.findViewById<EditText>(R.id.pca_course_search_edit_text)
            val courseListView = dialog.findViewById<ListView>(R.id.pca_course_list_view)
            val adapter: ArrayAdapter<Course> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                viewModel.coursesList
            )
            courseListView.adapter = adapter
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
                        viewModel.searchForCourse(searchEditText.text.toString(), adapter)
                    }
//                    adapter.clear()
//                    adapter.addAll(viewModel.coursesList)
                    adapter.notifyDataSetChanged()
//                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })
            courseListView.onItemClickListener = OnItemClickListener { _, _, position, _ -> // when item selected from list
                // set selected item on textView
                courseSpinner.text = adapter.getItem(position).toString()

                // Dismiss dialog
                dialog.dismiss()
            }
        }

    }


}