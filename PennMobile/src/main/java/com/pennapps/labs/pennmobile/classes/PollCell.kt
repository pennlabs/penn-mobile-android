package com.pennapps.labs.pennmobile.classes

class PollCell(poll: Poll) : HomeCell2() {
    var poll: Poll
    init{
        type = "poll"
        this.poll = poll
    }
}