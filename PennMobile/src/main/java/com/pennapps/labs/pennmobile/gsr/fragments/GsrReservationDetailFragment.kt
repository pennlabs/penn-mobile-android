package com.pennapps.labs.pennmobile.gsr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.databinding.FragmentGsrReservationDetailBinding


class GsrReservationDetailFragment : Fragment() {

    companion object {
        private const val ARG_BOOKING_ID = "booking_id"

        fun newInstance(bookingId: String): GsrReservationDetailFragment {
            return GsrReservationDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_BOOKING_ID, bookingId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGsrReservationDetailBinding.inflate(inflater, container, false)
        val bookingId = arguments?.getString(ARG_BOOKING_ID)

        // need to fetch full reservation details from API using bookingId
        // right now, just show booking id
        binding.gsrDetailBookingIdTv.text = "Booking ID: $bookingId"

        return binding.root
    }
}