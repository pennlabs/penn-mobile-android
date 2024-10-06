package com.pennapps.labs.pennmobile.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.HomeBaseCardBinding

open class HomeBaseHolder(
    val itemBinding: HomeBaseCardBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    var homeTitle = itemBinding.homeCardTitle
    var homeSubtitle = itemBinding.homeCardSubtitle
    var homeRv = itemBinding.homeCardRv
    var diningPrefsBtn = itemBinding.diningPrefsBtn
}
