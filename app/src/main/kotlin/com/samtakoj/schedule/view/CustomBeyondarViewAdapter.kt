package com.samtakoj.schedule.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.beyondar.android.view.BeyondarViewAdapter
import com.beyondar.android.view.OnClickBeyondarObjectListener
import com.beyondar.android.world.BeyondarObject
import com.samtakoj.schedule.R
import java.util.ArrayList

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
    }

    override fun getView(beyondarObject: BeyondarObject?, recycledView: View?, parent: ViewGroup?): View? {
        if (!showViewOn.contains(beyondarObject)) {
            return null
        }

        var view = recycledView
        if (recycledView == null) {
            view = inflater.inflate(R.layout.object_view, null)
        }

        val arrString = arrayListOf<String>("test","test","test","test","test","test","test","test","test","test","test","test")
        val listView = view?.findViewById(R.id.testListView) as ListView
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, arrString)
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
        val textView = view?.findViewById(R.id.info) as TextView
        textView.text = "${beyondarObject?.name} -> ${"%.0f".format(beyondarObject?.distanceFromUser)}m"

        setPosition(beyondarObject?.screenPositionBottomLeft)

        return view
    }
}