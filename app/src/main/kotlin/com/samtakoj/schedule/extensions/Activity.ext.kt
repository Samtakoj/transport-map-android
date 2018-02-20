package com.samtakoj.schedule.extensions

import android.app.Activity
import com.samtakoj.schedule.TransportApplication

/**
 * Created by artsiom.chuiko on 20/02/2018.
 */
fun Activity.transportApp() : TransportApplication {
    return this.application as TransportApplication
}