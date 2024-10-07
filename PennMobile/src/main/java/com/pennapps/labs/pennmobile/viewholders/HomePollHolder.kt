package com.pennapps.labs.pennmobile.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.PollCardBinding

class HomePollHolder(
    val itemBinding: PollCardBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    var pollTitle = itemBinding.homeCardTitle
    var pollSubtitle = itemBinding.homeCardSubtitle
    var pollSubtitle2 = itemBinding.homeCardSubtitle2
    var homeCardRv = itemBinding.homeCardRv
    var voteBtn = itemBinding.voteBtn
}
