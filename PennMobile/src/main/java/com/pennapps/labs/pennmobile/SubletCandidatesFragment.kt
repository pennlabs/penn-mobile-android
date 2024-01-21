package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.databinding.FragmentSubletCandidatesBinding

class SubletCandidatesFragment : Fragment() {
    private var _binding: FragmentSubletCandidatesBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSubletCandidatesBinding.inflate(inflater, container, false)

        return binding.root
    }

}