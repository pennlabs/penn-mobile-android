package com.pennapps.labs.pennmobile.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.HomeGsrCardBinding

class HomeGSRHolder(
    val itemBinding: HomeGsrCardBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    var homeGSRTitle = itemBinding.homeGsrTitle
    var homeGSRSubtitle = itemBinding.homeGsrSubtitle
    var homeGSRRv = itemBinding.homeGsrRv
    var homeGSRButton = itemBinding.homeGsrButton
}
