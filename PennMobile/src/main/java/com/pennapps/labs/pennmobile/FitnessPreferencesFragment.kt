package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.adapters.FitnessPreferenceAdapter
import com.pennapps.labs.pennmobile.classes.FitnessPreferenceViewModel

interface CloseListener {
    fun updateAdapters()
}

class FitnessPreferencesFragment(
    private val dataModel: FitnessPreferenceViewModel,
    private val listener: CloseListener,
) : DialogFragment() {
    private lateinit var mActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mActivity = activity as MainActivity
        return inflater.inflate(R.layout.fragment_fitness_preferences, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val prefAdapter = FitnessPreferenceAdapter(dataModel)

        val recyclerView: RecyclerView = view.findViewById(R.id.fitness_preference_recycler_view)
        recyclerView.adapter = prefAdapter

        val cancelText: TextView = view.findViewById(R.id.fitness_fragment_pref_cancel)
        cancelText.setOnClickListener {
            dataModel.restorePreferences()
            dialog?.dismiss()
        }

        val saveText: TextView = view.findViewById(R.id.fitness_fragment_pref_save)
        saveText.setOnClickListener {
            dataModel.updatePositionMap()
            dataModel.updateRemotePreferences(mActivity)
            listener.updateAdapters()

            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog

        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }
}
