package com.samtakoj.schedule.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.samtakoj.schedule.R
import com.samtakoj.schedule.RouteActivity
import com.samtakoj.schedule.api.ScheduleHelper
import com.samtakoj.schedule.extensions.transportApp
import com.samtakoj.schedule.model.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.withAlpha

class RouteListViewAdapter(val activity: Activity , val routes: MutableList<RouteCsv>): ArrayAdapter<RouteCsv>(activity, 0) {

    class ViewHolder(val textView: TextView, val numRouteTextView: TextView, val isFavoritesImageView: ImageView, val addTextView: TextView)

    private val mLock = Any()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var tempConvertView = convertView
        val routeBox = activity.transportApp().boxStore.boxFor(RouteCsv::class.java)

        if(tempConvertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            tempConvertView = inflater.inflate(R.layout.route_item, parent, false)
            viewHolder = ViewHolder(tempConvertView.findViewById(R.id.routeTextView) as TextView,
                    tempConvertView.findViewById(R.id.numRouteTextView) as TextView,
                    tempConvertView.findViewById(R.id.imageView) as ImageView,
                    tempConvertView.findViewById(R.id.addTextView) as TextView)
            tempConvertView.tag = viewHolder
        } else {
            viewHolder = tempConvertView.tag as ViewHolder
        }

        val item = getItem(position)
        viewHolder.textView.text = item.name
        viewHolder.numRouteTextView.text = item.num
        viewHolder.numRouteTextView.backgroundColor = getColorByTransportType(item.transportType)
        val imageResource = if(item.isFavorites) {
            R.drawable.star_filled
        } else {
            R.drawable.star
        }
        if(activity is RouteActivity) {
            viewHolder.isFavoritesImageView.visibility = View.INVISIBLE
        }
        viewHolder.isFavoritesImageView.setImageResource(imageResource)
        viewHolder.isFavoritesImageView.onClick {
            item.isFavorites = !item.isFavorites
            routeBox.put(item)
            val image = if(item.isFavorites) {
                R.drawable.star_filled
            } else {
                R.drawable.star
            }
            viewHolder.isFavoritesImageView.setImageResource(image)
        }
        viewHolder.addTextView.visibility = View.INVISIBLE
        tempConvertView!!.backgroundColorResource = android.R.color.white

        val scheduleHelper = ScheduleHelper(activity)
        val result = scheduleHelper.getInfoForRoute(item)
        if(result != "") {
            tempConvertView.backgroundColor = getColorByTransportType(item.transportType).withAlpha(50)
            viewHolder.addTextView.visibility = View.VISIBLE

            viewHolder.addTextView.text = result
        }

        return tempConvertView
    }

    private fun getColorByTransportType(transportType: String): Int {
        return when(transportType) {
            "bus" -> Color.argb(255, 237, 149, 42)
            "trol" -> Color.argb(255, 79, 173, 101)
            "metro" -> Color.argb(255, 39, 59, 122)
            "tram" -> Color.argb(255, 244, 75, 63)
            else -> -1
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