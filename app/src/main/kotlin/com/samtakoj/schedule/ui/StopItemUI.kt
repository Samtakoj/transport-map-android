package com.samtakoj.schedule.ui

import android.view.*
import android.widget.TextView
import org.jetbrains.anko.*
import com.samtakoj.schedule.R
import kotlin.properties.Delegates

class StopItemUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        var stopName: TextView by Delegates.notNull()
        return with(ui) {
            relativeLayout {
                backgroundResource = R.color.textColorPrimary
                stopName = textView("TextView") {
                    id = Ids.stopTextView
                    textColor = R.color.colorPrimary
                    textSize = 20f
                }.lparams(width = wrapContent, height = wrapContent) {
                    centerHorizontally()
                    centerVertically()
                }
            }
            let {
                it.view.tag = StopItemViewHolder(stopName)
                it.view
            }
        }
    }

    private object Ids {
        val stopTextView = View.generateViewId()
    }
}

class StopItemViewHolder(val stopText: TextView) {
    fun bind(name: String) {
        stopText.text = name
    }
}