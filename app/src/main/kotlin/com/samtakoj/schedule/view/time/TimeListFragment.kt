package com.samtakoj.schedule.view.time

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samtakoj.schedule.R
import com.samtakoj.schedule.view.TimeListViewAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.ctx

class TimeListFragment(val listAdapter: TimeListViewAdapter): Fragment(), AnkoLogger {

    companion object {
        fun newInstance(listAdapter: TimeListViewAdapter): TimeListFragment {
            val fragment: TimeListFragment = TimeListFragment(listAdapter)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return TimeListUI(this.listAdapter).createView(AnkoContext.Companion.create(ctx, this))
    }

}

class TimeListUI(val listAdapter: TimeListViewAdapter): AnkoComponent<Fragment> {
    override fun createView(ui: AnkoContext<Fragment>): View {
        return with(ui) {
            listView {
                id = R.id.time_list_view
                adapter = listAdapter
            }
        }
    }
}