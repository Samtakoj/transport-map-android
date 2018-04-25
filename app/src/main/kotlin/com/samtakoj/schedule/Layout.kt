package com.samtakoj.schedule

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import com.google.android.gms.maps.SupportMapFragment
import com.samtakoj.schedule.view.particle.ParticleView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.sdk25.coroutines.onClick

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
                title = ui.resources.getString(R.string.app_name_main)
            }.lparams(width = matchParent, height = wrapContent).let {
                (ui.ctx  as TestActivity).setSupportActionBar(it)
            }
            frameLayout {
                id = ContainerID
                button("Show Map") {
                    onClick {
                        startActivity<GoogleMapActivity>()
                    }
                }.lparams {
                    gravity = Gravity.BOTTOM
                }
            }.lparams(width = matchParent, height = matchParent, weight = 1f)
        }
    }
}

inline fun ViewManager.particleView(theme: Int = 0, init: ParticleView.() -> Unit) = ankoView(::ParticleView, theme, init)

class SplashActivityUi: AnkoComponent<SplashActivity> {
    override fun createView(ui: AnkoContext<SplashActivity>) = with(ui) {
        verticalLayout {
            lparams(width = matchParent, height = matchParent)
            particleView {
                lparams(width = matchParent, height = matchParent)
                backgroundColor = Color.argb(255, 237, 149, 42)
                particleColor = Color.argb(255, 255, 255, 255)
                particleTextSize = sp(27)
                hostText = "Transport"
                particleText = "MINSK"
            }.let {
                it.startAnim()
                it.setOnParticleAnimListener {
                    ApplicationPermissions.requestBasic(ui.ctx  as SplashActivity, ui.ctx as SplashActivity)
                }
            }
        }
    }
}