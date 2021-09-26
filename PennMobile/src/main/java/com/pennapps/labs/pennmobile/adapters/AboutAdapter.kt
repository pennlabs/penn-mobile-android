package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import kotlinx.android.synthetic.main.team_member.view.*

class AboutAdapter(private var members: ArrayList<String>)
    : RecyclerView.Adapter<AboutAdapter.TeamViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.team_member, parent, false)
        mContext = parent.context
        return TeamViewHolder(view)
    }

    override fun getItemCount(): Int {
        return members.count()
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.view.name_tv?.text = members[position]
        var imageId = when (members[position]) {
            "Marta García Ferreiro" -> R.drawable.marta
            "Varun Ramakrishnan" -> R.drawable.varun
            "Anna Wang" -> R.drawable.anna
            "Davies Lumumba" -> R.drawable.davies
            "Sophia Ye" -> R.drawable.sophia
            "Sahit Penmatcha" -> R.drawable.sahit
            "Vishesh Patel" -> R.drawable.vishesh
            "Awad Irfan" -> R.drawable.awad
            "Liz Powell" -> R.drawable.liz
            "Anna Jiang" -> R.drawable.anna_jiang
            "Rohan Chhaya" -> R.drawable.rohan
            "Julius Snipes" -> R.drawable.julius
            else -> null
        }
        if (android.os.Build.VERSION.SDK_INT >=  android.os.Build.VERSION_CODES.M){
            if (imageId != null) holder.view.person_iv?.setImageResource(imageId)
        }

    }

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}