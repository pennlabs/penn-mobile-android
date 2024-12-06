package com.pennapps.labs.pennmobile.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.DiningPredictionsCardBinding

class DiningPredictionsHolder(
    itemBinding: DiningPredictionsCardBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    internal var predictionsTitle = itemBinding.predictionsTitle
    internal var diningPredictionsGraph = itemBinding.diningPredictionsGraph
    internal var extraAmount = itemBinding.extraAmount
    internal var extra = itemBinding.extra
    internal var extraNote = itemBinding.extraNote
}
