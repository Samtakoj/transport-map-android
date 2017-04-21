package com.samtakoj.schedule.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.samtakoj.schedule.R
import com.samtakoj.shedule.model.StopCsv

class TimeListViewAdapter(val context: Context , val time: Map<Long, List<Long>>): BaseAdapter() {

    class ViewHolder(val hourTextView: TextView, val minutesTextView: TextView)

    private val mLock = Any()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var tempConvertView = convertView

        if(tempConvertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            tempConvertView = inflater.inflate(R.layout.time_item, parent, false)
            viewHolder = ViewHolder(tempConvertView.findViewById(R.id.hourTextView) as TextView, tempConvertView.findViewById(R.id.minutesTextView) as TextView)
            tempConvertView.tag = viewHolder
        } else {
            viewHolder = tempConvertView.tag as ViewHolder
        }

        val hour = getItemId(position)
        val item = getItem(hour.toInt())
        viewHolder.hourTextView.text = hour.toString()
        viewHolder.minutesTextView.text = item.toString()

        return tempConvertView
    }

    override fun getItem(position: Int): List<Long>? {
        return time[position.toLong()]
    }

    override fun getItemId(position: Int): Long {
        return time.keys.elementAt(position)
    }

    override fun getCount(): Int {
        return time.count()
    }

//    fun addAll(collection: Map<Long, List<Long>>) {
//        synchronized(mLock) {
//            time.addAll(collection)
//        }
//        notifyDataSetChanged()
//    }
}