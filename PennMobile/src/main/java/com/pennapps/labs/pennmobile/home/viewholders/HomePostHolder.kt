package com.pennapps.labs.pennmobile.home.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.HomePostCardBinding

class HomePostHolder(
    val itemBinding: HomePostCardBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    var homePostTitle = itemBinding.homePostTitle
    var homePostSubtitle = itemBinding.homePostSubtitle
    var homePostTimestamp = itemBinding.homePostTimestamp
    var homePostContainer = itemBinding.postCardContainer
    var homePostSource = itemBinding.homePostSource
    var homePostIv = itemBinding.homePostIv
    var homePostCard = itemBinding.homePostCard
    var postBlurView = itemBinding.postBlurView
}
