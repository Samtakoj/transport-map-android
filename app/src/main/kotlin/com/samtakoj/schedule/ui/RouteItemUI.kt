package com.samtakoj.schedule.layouts

import android.view.*
import org.jetbrains.anko.*
import com.samtakoj.schedule.R

class RouteItemUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            relativeLayout {
                backgroundColor = 0xffffff.opaque
//            orientation = LinearLayout.VERTICAL
                textView("TextView") {
                    backgroundColor = 0xed952a.opaque
                    id = Ids.numRouteTextView
                    setPadding(dip(8), dip(8), dip(8), dip(8))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    textColor = 0xfff.opaque
                    textSize = 20f
                }.lparams(width = dip(40), height = dip(40)) {
                    alignParentStart()
                    centerHorizontally()
                    centerVertically()
                }
                textView("TextView") {
                    gravity = Gravity.CENTER_VERTICAL
                    id = Ids.routeTextView
                    textColor = 0x000000.opaque
                    textSize = 20f
                }.lparams(width = dip(120), height = dip(60)) {
                    centerVertically()
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