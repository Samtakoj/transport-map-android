package com.samtakoj.schedule.view

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.samtakoj.schedule.ui.TimeItemUI
import com.samtakoj.schedule.ui.TimeItemViewHolder
import org.jetbrains.anko.AnkoContext

class TimeListViewAdapter(val time: MutableMap<Long, List<Long>>): BaseAdapter() {

    private val mLock = Any()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var tempConvertView = convertView

        if(tempConvertView == null) {
            tempConvertView = TimeItemUI().createView(AnkoContext.create(parent.context, parent))
        }

        val hour = getItemId(position)
        val item = getItem(hour.toInt())
        (tempConvertView.tag as TimeItemViewHolder).bind(hour, item)

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

    fun clear() {
        synchronized(mLock) {
            this.time.clear()
        }
        notifyDataSetChanged()
    }

    fun addAll(collection: MutableMap<Long, List<Long>>) {
        synchronized(mLock) {
            time.putAll(collection)
        }
        notifyDataSetChanged()
    }

    fun getAll(): MutableMap<Long, List<Long>> {
        return time
    }
}