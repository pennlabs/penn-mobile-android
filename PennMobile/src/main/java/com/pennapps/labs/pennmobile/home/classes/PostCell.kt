package com.pennapps.labs.pennmobile.home.classes

data class PostCell(
    val post: Post,
) : HomeCell() {
    init {
        type = "post"
    }
}
