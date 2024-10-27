package com.pennapps.labs.pennmobile.dining.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.DiningBalancesCardBinding

class DiningBalancesCardHolder(
    itemBinding: DiningBalancesCardBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    internal var diningDollarsAmount = itemBinding.diningDollarsAmount
    internal var swipesAmount = itemBinding.swipesAmount
    internal var guestSwipesAmount = itemBinding.guestSwipesAmount
}
