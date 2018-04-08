package com.samtakoj.schedule.ui

import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.TextView
import org.jetbrains.anko.*
import kotlin.properties.Delegates

class TimeItemUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        var hourView: TextView by Delegates.notNull()
        var minutesView: TextView by Delegates.notNull()
        val viewItem = with(ui) {
            relativeLayout {
                backgroundColor = ContextCompat.getColor(ctx, android.R.color.white)
                lparams(matchParent, matchParent)

                hourView = textView("TextView") {
                    id = hourTextView
                    textColor = 0x000.opaque
                    textSize = 24f
                }.lparams(width = wrapContent, height = wrapContent) {
                    alignParentStart()
                    centerVertically()
                    marginStart = dip(24)
                }
                minutesView = textView("TextView") {
                    id = minutesTextView
                    textColor = 0x000.opaque
                    textSize = 20f
                }.lparams(width = wrapContent, height = wrapContent) {
                    centerVertically()
                    endOf(hourTextView)
                    marginStart = dip(33)
                }
            }
        }
        viewItem.tag = TimeItemViewHolder(hourView, minutesView)
        return viewItem
    }

    companion object {
        val hourTextView = View.generateViewId()
        val minutesTextView = View.generateViewId()
    }
}

class TimeItemViewHolder(val hourView: TextView, val minutesView: TextView) {
    fun bind(hour: Long, item: List<Long>?) {
        hourView.text = (hour % 24).toString()
        minutesView.text = item.toString().replace(Regex.fromLiteral("[,]"), "")
    }
}