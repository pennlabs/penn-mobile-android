package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import kotlinx.android.synthetic.main.team_member.view.name_tv
import kotlinx.android.synthetic.main.team_member.view.person_iv

class AboutAdapter(
    private var members: ArrayList<String>,
) : RecyclerView.Adapter<AboutAdapter.TeamViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.team_member, parent, false)
        mContext = parent.context
        return TeamViewHolder(view)
    }

    override fun getItemCount(): Int = members.count()

    override fun onBindViewHolder(
        holder: TeamViewHolder,
        position: Int,
    ) {
        holder.view.name_tv?.text = members[position]
        val imageId =
            when (members[position]) {
                "Marta García Ferreiro" -> R.drawable.marta
                "Varun Ramakrishnan" -> R.drawable.varun
                "Anna Wang" -> R.drawable.anna
                "Davies Lumumba" -> R.drawable.davies
                "Sophia Ye" -> R.drawable.sophia
                "Sahit Penmatcha" -> R.drawable.sahit
                "Awad Irfan" -> R.drawable.awad
                "Liz Powell" -> R.drawable.liz
                "Anna Jiang" -> R.drawable.anna_jiang
                "Rohan Chhaya" -> R.drawable.rohan
                "Julius Snipes" -> R.drawable.julius
                "Ali Krema" -> R.drawable.ali
                "Trini Feng" -> R.drawable.trini
                "Vedha Avali" -> R.drawable.vedha
                "Aaron Mei" -> R.drawable.aaron
                "Joe MacDougall" -> R.drawable.joe
                "Baron Ping-Yeh Hsieh" -> R.drawable.baron
                "David Fu" -> R.drawable.david
                else -> null
            }
        if (imageId != null) holder.view.person_iv?.setImageResource(imageId)
    }

    inner class TeamViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
