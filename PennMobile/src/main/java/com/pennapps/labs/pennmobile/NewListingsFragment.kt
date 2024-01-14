package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.databinding.FragmentNewListingsBinding
import com.pennapps.labs.pennmobile.databinding.FragmentSubletterPostedListingsBinding

class NewListingsFragment : Fragment() {
    private var _binding: FragmentNewListingsBinding? = null
    private val binding get() = _binding!!
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
}