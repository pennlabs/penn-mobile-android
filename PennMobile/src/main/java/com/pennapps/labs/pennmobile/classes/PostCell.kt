package com.pennapps.labs.pennmobile.classes

data class PostCell(val post: Post) : HomeCell() {
    init {
        type = "post" 
    }
}
