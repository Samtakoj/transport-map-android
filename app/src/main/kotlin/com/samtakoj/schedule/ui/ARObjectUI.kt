package com.samtakoj.schedule.ui

import android.view.*
import android.widget.*
import org.jetbrains.anko.*

class ARObjectUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            linearLayout {
                lparams(matchParent, matchParent)
                orientation = LinearLayout.VERTICAL
                weightSum = 1f

                textView("TextView") {
                    backgroundResource = android.R.color.black
                    id = Ids.info
                    textColorResource = android.R.color.white
                }.lparams(width = wrapContent, height = wrapContent) {
                    weight = 1f
                }
            }
        }
    }

    object Ids {
        val info = View.generateViewId()
    }
}