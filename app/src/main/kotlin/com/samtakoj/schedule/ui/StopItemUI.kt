package com.samtakoj.schedule.ui

import android.view.*
import org.jetbrains.anko.*
import com.samtakoj.schedule.R

class StopItemUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            relativeLayout {
                backgroundResource = R.color.textColorPrimary
//                orientation = LinearLayout.VERTICAL

                textView("TextView") {
                    id = Ids.stopTextView
                    textColor = R.color.colorPrimary
                    textSize = 20f
                }.lparams(width = wrapContent, height = wrapContent) {
                    centerHorizontally()
                    centerVertically()
                }
            }
        }
    }

    private object Ids {
        val stopTextView = View.generateViewId()
    }
}