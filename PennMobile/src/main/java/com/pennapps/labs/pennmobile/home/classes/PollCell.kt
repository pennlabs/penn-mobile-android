package com.pennapps.labs.pennmobile.home.classes

class PollCell(
    poll: Poll,
) : HomeCell() {
    var poll: Poll

    init {
        type = "poll"
        this.poll = poll
    }
}
