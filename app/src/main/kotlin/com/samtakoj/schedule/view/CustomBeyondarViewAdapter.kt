package com.samtakoj.schedule.view

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
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
import org.jetbrains.anko.*
import kotlin.collections.ArrayList

/**
 * Created by artsiom.chuiko on 19/04/2017.
 */
class CustomBeyondarViewAdapter(val activity: Activity): BeyondarViewAdapter(activity), OnClickBeyondarObjectListener {
    val showViewOn = ArrayList<BeyondarObject>()
    val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onClickBeyondarObject(beyondarObjects: ArrayList<BeyondarObject>?) {
        if (beyondarObjects?.size == 0) return
        val beyondarObject = beyondarObjects?.get(0)
        if (showViewOn.contains(beyondarObject)) {
            showViewOn.remove(beyondarObject)
        } else {
            showViewOn.add(beyondarObject as BeyondarObject)
        }

        val routeBox = activity.transportApp().boxStore.boxFor(RouteCsv::class.java)
        val routeList = routeBox.query().contains(RouteCsv_.stops, beyondarObject!!.id.toString()).order(RouteCsv_.num).build().find()

        context.startActivity<RouteActivity>("routes" to routeList, "stopName" to beyondarObject.name)

    }

    override fun getView(beyondarObject: BeyondarObject?, recycledView: View?, parent: ViewGroup?): View? {
//        if (!showViewOn.contains(beyondarObject)) {
//            return null
//        }

//        var view = recycledView
//        if (recycledView == null) {
//        }
        var view = inflater.inflate(R.layout.object_view, null)


//        val listView = view?.findViewById(R.id.testListView) as ListView
//        val adapter = RouteListViewAdapter(context, routeList as ArrayList<RouteCsv>)
//        listView.adapter = adapter
//        adapter.notifyDataSetChanged()
//
//        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            Log.i("TEST", "itemClick: position = $position, id = $id")
//
//            context.startActivity<MainActivity>()
//        }

        val textView = view?.findViewById(R.id.info) as TextView
        textView.text = "${beyondarObject?.name} -> ${"%.0f".format(beyondarObject?.distanceFromUser)}m"

        setPosition(beyondarObject?.screenPositionBottomLeft)

        return view
    }
}