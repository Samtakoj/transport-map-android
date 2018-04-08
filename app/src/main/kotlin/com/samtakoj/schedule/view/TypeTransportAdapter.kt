package com.samtakoj.schedule.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.samtakoj.schedule.view.tab.RouteListFragment
import com.samtakoj.schedule.view.tab.SmartFragmentStatePagerAdapter

class TypeTransportAdapter(fm: FragmentManager, var listAdapter: RouteListViewAdapter): SmartFragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return RouteListFragment.newInstance(listAdapter, getPageTitle(position))
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): String {
        return when (position) {
            0 -> "bus"
            1 -> "troll"
            2 -> "tram"
            3 -> "under"
            else -> ""
        }
    }

    fun setAdapter(newAdapter: RouteListViewAdapter, position: Int): Fragment {
        listAdapter = newAdapter
        return RouteListFragment.newInstance(listAdapter, getPageTitle(position))
    }
}