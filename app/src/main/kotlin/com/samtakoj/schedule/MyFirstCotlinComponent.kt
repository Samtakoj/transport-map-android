package com.samtakoj.schedule

import android.view.View
import org.jetbrains.anko.*

class MyFirstCotlinComponent : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        relativeLayout {
            padding = dip(20)

            textView {
                text = "Test"
            }
        }
    }
}
