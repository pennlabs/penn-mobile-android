package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.TeamMemberBinding


class AboutAdapter(
    private var members: ArrayList<String>,
) : RecyclerView.Adapter<AboutAdapter.TeamViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TeamViewHolder {
        mContext = parent.context
        val itemBinding = TeamMemberBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return TeamViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = members.count()

    override fun onBindViewHolder(
        holder: TeamViewHolder,
        position: Int,
    ) {
        holder.name_tv.text = members[position]
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
                else -> null
            }
        if (imageId != null) holder.person_iv.setImageResource(imageId)
    }

    inner class TeamViewHolder(
        itemBinding: TeamMemberBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val person_iv: ImageView = itemBinding.personIv
        val name_tv: TextView = itemBinding.nameTv
    }
}
