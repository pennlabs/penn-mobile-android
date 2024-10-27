package com.pennapps.labs.pennmobile.home.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.HomeNewsCardBinding

class HomeNewsCardHolder(
    val itemBinding: HomeNewsCardBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    var homeNewsTitle = itemBinding.homeNewsTitle
    var homeNewsSubtitle = itemBinding.homeNewsSubtitle
    var homeNewsTimestamp = itemBinding.homeNewsTimestamp
    var homeNewsImageView = itemBinding.homeNewsIv
    var newsCardLogo = itemBinding.newsCardLogo
    var newsInfoIcon = itemBinding.newsInfoIcon
    var newsBlurView = itemBinding.blurView
    var newsCardContainer = itemBinding.newsCardContainer
    var newsButton = itemBinding.button
    var dotDivider = itemBinding.dotDivider
}
