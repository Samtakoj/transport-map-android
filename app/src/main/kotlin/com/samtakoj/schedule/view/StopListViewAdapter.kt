package com.samtakoj.schedule.view

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.ui.StopItemUI
import com.samtakoj.schedule.ui.StopItemViewHolder
import org.jetbrains.anko.AnkoContext

class StopListViewAdapter(val stops: ArrayList<StopCsv>): BaseAdapter() {
    private val mLock = Any()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var tempConvertView = convertView

        if(tempConvertView == null) {
            tempConvertView = StopItemUI().createView(AnkoContext.create(parent?.context, parent))
        }

        val item = getItem(position)
        (tempConvertView.tag as StopItemViewHolder).bind(item.name)
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