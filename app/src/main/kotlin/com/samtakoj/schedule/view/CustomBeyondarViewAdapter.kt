package com.samtakoj.schedule.view

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.beyondar.android.view.BeyondarViewAdapter
import com.beyondar.android.view.OnClickBeyondarObjectListener
import com.beyondar.android.world.BeyondarObject
import com.samtakoj.schedule.*
import com.samtakoj.schedule.extensions.transportApp
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.RouteCsv_
import com.samtakoj.schedule.ui.ARObjectUI
import org.jetbrains.anko.*
import kotlin.collections.ArrayList

/**
 * Created by artsiom.chuiko on 19/04/2017.
 */
class CustomBeyondarViewAdapter(val activity: Activity): BeyondarViewAdapter(activity), OnClickBeyondarObjectListener {

    override fun onClickBeyondarObject(beyondarObjects: ArrayList<BeyondarObject>?) {
        if (beyondarObjects?.size == 0) return
        val beyondarObject = beyondarObjects?.get(0)

        val routeBox = activity.transportApp().boxStore.boxFor(RouteCsv::class.java)
        val routeList = routeBox.query().contains(RouteCsv_.stops, beyondarObject!!.id.toString()).order(RouteCsv_.num).build().find()

        context.startActivity<RouteActivity>("routes" to routeList, "stopName" to beyondarObject.name)

    }

    override fun getView(beyondarObject: BeyondarObject?, recycledView: View?, parent: ViewGroup): View? {
        val view = ARObjectUI().createView(AnkoContext.create(parent.context, parent))
        val textView = view.findViewById(ARObjectUI.Ids.info) as TextView
        textView.text = "${beyondarObject?.name} -> ${"%.0f".format(beyondarObject?.distanceFromUser)}m"

        setPosition(beyondarObject?.screenPositionBottomLeft)
        return view
    }
}