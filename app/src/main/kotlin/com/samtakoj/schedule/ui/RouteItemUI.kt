package com.samtakoj.schedule.ui

import android.support.v4.content.ContextCompat
import android.view.*
import org.jetbrains.anko.*
import com.samtakoj.schedule.R

class RouteItemUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            relativeLayout {
                backgroundColor = ContextCompat.getColor(ctx, android.R.color.white)
                textView("TextView") {
                    backgroundColor = 0xed952a
                    id = Ids.numRouteTextView
                    setPadding(dip(8), dip(8), dip(8), dip(8))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    textColor = 0xfff
                    textSize = 20f
                }.lparams(width = dip(40), height = dip(40)) {
                    alignParentStart()
//                    centerHorizontally()
                    centerVertically()
                    marginStart = dip(16)
                    setPadding(0, dip(8), 0, 0)
                }
                textView("TextView") {
                    gravity = Gravity.CENTER_VERTICAL
                    id = Ids.routeTextView
                    textColor = 0x000000
                    textSize = 20f
                }.lparams(width = dip(120), height = dip(60)) {
                    centerVertically()
                    marginStart = dip(24)
                    marginEnd = dip(12)
                    endOf(Ids.numRouteTextView)
                    startOf(Ids.imageView)
                    gravity = Gravity.CENTER
                }
                imageView {
                    id = Ids.imageView
                    setImageResource(R.drawable.star)
                }.lparams(width = dip(30), height = dip(30)) {
                    alignParentEnd()
                    centerVertically()
                }
            }
        }
    }


    private object Ids {
        val imageView = View.generateViewId()
        val numRouteTextView = View.generateViewId()
        val routeTextView = View.generateViewId()
    }
}