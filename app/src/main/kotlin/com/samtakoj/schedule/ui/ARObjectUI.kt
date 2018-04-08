package com.samtakoj.schedule.layouts

import android.view.*
import android.widget.*
import org.jetbrains.anko.*
import com.samtakoj.schedule.R

class ARObjectUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            linearLayout {
                orientation = LinearLayout.VERTICAL
                weightSum = 1f

                textView("TextView") {
                    backgroundResource = android.R.color.black
                    id = Ids.info
                    textColor = R.color.colorPrimary
                }.lparams(width = wrapContent, height = wrapContent) {
                    weight = 1f
                }
            }
        }
    }

    private object Ids {
        val info = View.generateViewId()
    }
}