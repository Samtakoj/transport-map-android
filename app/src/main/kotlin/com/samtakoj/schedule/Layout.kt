package com.samtakoj.schedule

import android.support.v4.content.ContextCompat
import android.view.View
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar

/**
 * Created by artsiom.chuiko on 30/03/2017.
 */
class MainActivityUi: AnkoComponent<TestActivity> {
    companion object {
        val StatusID = View.generateViewId()
        val ToolbarID = View.generateViewId()
        val LocationID = View.generateViewId()
        val ContainerID = View.generateViewId()
    }

    override fun createView(ui: AnkoContext<TestActivity>) = with(ui) {
        verticalLayout {
            lparams(width = matchParent, height = matchParent)
            gravity = 30
            backgroundColor = ContextCompat.getColor(ctx, android.R.color.white)
            toolbar {
                id = ToolbarID
                backgroundColor = ContextCompat.getColor(ctx, android.R.color.white)
            }.lparams(width = matchParent, height = wrapContent).let {
                (ui.ctx  as TestActivity).setSupportActionBar(it)
            }

            frameLayout {
                id = ContainerID
                lparams(width = matchParent, height = matchParent, weight = 1f)
            }
        }
    }
}

class SplashActivityUi: AnkoComponent<SplashActivity> {
    override fun createView(ui: AnkoContext<SplashActivity>): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}