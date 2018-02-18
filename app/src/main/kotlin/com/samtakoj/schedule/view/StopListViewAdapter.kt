package com.samtakoj.schedule.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.samtakoj.schedule.R
import com.samtakoj.schedule.model.StopCsv

class StopListViewAdapter(val context: Context , val stops: ArrayList<StopCsv>): BaseAdapter() {

    class ViewHolder(val textView: TextView)

    private val mLock = Any()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var tempConvertView = convertView

        if(tempConvertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            tempConvertView = inflater.inflate(R.layout.stop_item, parent, false)
            viewHolder = ViewHolder(tempConvertView.findViewById(R.id.stopTextView) as TextView)
            tempConvertView.tag = viewHolder
        } else {
            viewHolder = tempConvertView.tag as ViewHolder
        }

        val item = getItem(position)
        viewHolder.textView.text = item.name

        return tempConvertView
    }

    override fun getItem(position: Int): StopCsv {
        return stops[position]
    }

    override fun getItemId(position: Int): Long {
        return stops[position].id
    }

    override fun getCount(): Int {
        return stops.count()
    }

    fun addAll(collection: Collection<StopCsv>) {
        synchronized(mLock) {
            stops.addAll(collection)
        }
        notifyDataSetChanged()
    }
}