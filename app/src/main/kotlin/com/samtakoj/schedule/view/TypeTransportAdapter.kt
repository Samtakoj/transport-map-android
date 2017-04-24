package com.samtakoj.schedule.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.samtakoj.schedule.view.tab.RouteListFragment
import com.samtakoj.schedule.view.tab.SmartFragmentStatePagerAdapter

class TypeTransportAdapter(fm: FragmentManager, val listAdapter: RouteListViewAdapter): SmartFragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return RouteListFragment.newInstance(listAdapter)
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): String {
        when (position) {
            0 -> return "bus"
            1 -> return "trol"
            2 -> return "tram"
            3 -> return "metro"
            else -> return ""
        }
    }
}