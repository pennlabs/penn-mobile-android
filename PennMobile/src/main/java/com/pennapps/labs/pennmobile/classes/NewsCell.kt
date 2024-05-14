package com.pennapps.labs.pennmobile.classes

data class NewsCell(val article: Article) : HomeCell() {
    init {
        type = "news"
    }
}
