package com.samtakoj.schedule.view

import android.content.Context
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.beyondar.android.util.ImageUtils
import com.beyondar.android.view.BeyondarViewAdapter
import com.beyondar.android.view.OnClickBeyondarObjectListener
import com.beyondar.android.world.BeyondarObject
import com.samtakoj.schedule.*
import com.samtakoj.shedule.model.RouteCsv
import com.samtakoj.shedule.model.RouteCsv_
import org.jetbrains.anko.*
import java.io.File
import kotlin.collections.ArrayList

/**
 * Created by artsiom.chuiko on 19/04/2017.
 */
class CustomBeyondarViewAdapter(context: Context): BeyondarViewAdapter(context), OnClickBeyondarObjectListener {
    val showViewOn = ArrayList<BeyondarObject>()
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onClickBeyondarObject(beyondarObjects: ArrayList<BeyondarObject>?) {
        if (beyondarObjects?.size == 0) return
        val beyondarObject = beyondarObjects?.get(0)
        if (showViewOn.contains(beyondarObject)) {
            showViewOn.remove(beyondarObject)
        } else {
            showViewOn.add(beyondarObject as BeyondarObject)
        }

        val routeBox = (context.applicationContext as TransportApplication).boxStore.boxFor(RouteCsv::class.java)
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