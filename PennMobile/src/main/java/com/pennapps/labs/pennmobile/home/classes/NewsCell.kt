package com.pennapps.labs.pennmobile.home.classes

data class NewsCell(
    val article: Article,
) : HomeCell() {
    init {
        type = "news"
    }
}
