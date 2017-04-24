package com.samtakoj.schedule.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.samtakoj.schedule.R
import com.samtakoj.shedule.model.RouteCsv

class RouteListViewAdapter(context: Context , val routes: ArrayList<RouteCsv>): ArrayAdapter<RouteCsv>(context, 0) {

    class ViewHolder(val textView: TextView)

    private val mLock = Any()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var tempConvertView = convertView

        if(tempConvertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            tempConvertView = inflater.inflate(R.layout.route_item, parent, false)
            viewHolder = ViewHolder(tempConvertView.findViewById(R.id.routeTextView) as TextView)
            tempConvertView.tag = viewHolder
        } else {
            viewHolder = tempConvertView.tag as ViewHolder
        }

        val item = getItem(position)
        viewHolder.textView.text = "${item.num}:\t${item.name}"

        return tempConvertView
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