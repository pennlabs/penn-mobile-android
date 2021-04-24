package com.pennapps.labs.pennmobile

import com.agoda.kakao.text.KTextView
import com.kaspersky.kaspresso.screens.KScreen

object MainScreen : KScreen<MainScreen>() {
    override val layoutId: Int? = R.layout.activity_main
    override val viewClass: Class<*>? = MainActivity::class.java

    val toolbarTitle = KTextView { withId(R.id.toolbar_title) }
}