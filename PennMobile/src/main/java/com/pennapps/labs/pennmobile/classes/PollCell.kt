package com.pennapps.labs.pennmobile.classes

class PollCell(poll: Poll) : HomeCell() {
    var poll: Poll
    init{
        type = "poll"
        this.poll = poll
    }
}