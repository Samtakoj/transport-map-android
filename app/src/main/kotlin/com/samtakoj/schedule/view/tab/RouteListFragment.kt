package com.samtakoj.schedule.view.tab

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.samtakoj.schedule.R
import com.samtakoj.schedule.view.RouteListViewAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.ctx

/**
 * Created by Александр on 23.04.2017.
 */
class RouteListFragment(val listAdapter: RouteListViewAdapter, val name: String, val itemClickListener: AdapterView.OnItemClickListener): Fragment(), AnkoLogger {

    companion object {
        fun newInstance(listAdapter: RouteListViewAdapter, name: String, itemClickListener: AdapterView.OnItemClickListener): RouteListFragment {
            val fragment: RouteListFragment = RouteListFragment(listAdapter, name, itemClickListener)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val resultView = RouteListUI(this.listAdapter).createView(AnkoContext.Companion.create(ctx, this))
        (resultView as ListView).onItemClickListener = itemClickListener
        return resultView
    }

}

class RouteListUI(val listAdapter: RouteListViewAdapter): AnkoComponent<Fragment> {
    override fun createView(ui: AnkoContext<Fragment>): View {
        return with(ui) {
            listView {
                id = R.id.route_list_view
                adapter = listAdapter
            }
        }
    }
}