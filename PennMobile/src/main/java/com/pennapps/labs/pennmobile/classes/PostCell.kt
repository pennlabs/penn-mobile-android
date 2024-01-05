package com.pennapps.labs.pennmobile.classes

data class PostCell(val post: Post) : HomeCell2() {
    init {
        type = "post" 
    }
}
