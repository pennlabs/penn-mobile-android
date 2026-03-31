package com.pennapps.labs.pennmobile.gsr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentGsrReservationDetailBinding
import com.squareup.picasso.Picasso
import org.joda.time.format.DateTimeFormat


class GsrReservationDetailFragment : Fragment() {

    companion object {
        private const val ARG_SHARE_CODE = "share_code"

        fun newInstance(shareCode: String): GsrReservationDetailFragment {
            return GsrReservationDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SHARE_CODE, shareCode)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGsrReservationDetailBinding.inflate(inflater, container, false)

        val shareCode = arguments?.getString(ARG_SHARE_CODE)
            ?: activity?.intent?.data?.getQueryParameter("data")

        if (shareCode == null) {
            binding.gsrDetailBookingIdTv.text = "Invalid reservation link."
            return binding.root
        }

        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "")

        MainActivity.studentLifeInstance
            .getReservationFromShareCode(bearerToken, shareCode)
            .subscribeOn(rx.schedulers.Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe({ reservation ->
                val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ")
                val from = formatter.parseDateTime(reservation.start)
                val to = formatter.parseDateTime(reservation.end)

                binding.gsrDetailLocationTv.text = "${reservation.gsr.name} - ${reservation.roomName}"
                binding.gsrDetailDateTv.text =
                    from.toString("EEEE, MMMM d") + "\n" +
                            from.toString("h:mm a") + " - " +
                            to.toString("h:mm a")
                binding.gsrDetailBookingIdTv.text = reservation.ownerName

                Picasso.get().load(reservation.gsr.imageUrl).fit().centerCrop().into(binding.gsrDetailIv)

            }, { error ->
                error.printStackTrace()
                android.util.Log.e("GSRDetail", "Error loading reservation: ${error.message}")
                binding.gsrDetailBookingIdTv.text = "Failed to load reservation."
            })

        return binding.root
    }
}