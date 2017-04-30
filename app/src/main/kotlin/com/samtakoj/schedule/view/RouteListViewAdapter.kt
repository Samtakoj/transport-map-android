package com.samtakoj.schedule.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.samtakoj.schedule.R
import com.samtakoj.shedule.model.RouteCsv
import org.jetbrains.anko.backgroundColor

class RouteListViewAdapter(context: Context , val routes: ArrayList<RouteCsv>): ArrayAdapter<RouteCsv>(context, 0) {

    class ViewHolder(val textView: TextView, val numRouteTextView: TextView)

    private val mLock = Any()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var tempConvertView = convertView

        if(tempConvertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            tempConvertView = inflater.inflate(R.layout.route_item, parent, false)
            viewHolder = ViewHolder(tempConvertView.findViewById(R.id.routeTextView) as TextView, tempConvertView.findViewById(R.id.numRouteTextView) as TextView)
            tempConvertView.tag = viewHolder
        } else {
            viewHolder = tempConvertView.tag as ViewHolder
        }

        val item = getItem(position)
        viewHolder.textView.text = item.name
        viewHolder.numRouteTextView.text = item.num
        viewHolder.numRouteTextView.backgroundColor = getColorByTransportType(item.transportType)

        return tempConvertView
    }

    private fun getColorByTransportType(transportType: String): Int {
        when(transportType) {
            "bus" -> return Color.argb(255, 11, 191, 214)
            "trol" -> return Color.argb(255, 43, 206, 81)
            "metro" -> return Color.argb(255, 39, 59, 122)
            "tram" -> return Color.argb(255, 244, 75, 63)
            else -> return -1
        }
    }

    override fun getItem(position: Int): RouteCsv {
        return routes[position]
    }

    override fun getItemId(position: Int): Long {
        return routes[position].id
    }

    override fun getCount(): Int {
        return routes.count()
    }

    override fun clear() {
        synchronized(mLock) {
            this.routes.clear()
        }
        notifyDataSetChanged()
    }

    override fun addAll(collection: Collection<RouteCsv>) {
        synchronized(mLock) {
            routes.addAll(collection)
        }
        notifyDataSetChanged()
    }
}