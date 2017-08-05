package com.samtakoj.schedule.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.samtakoj.schedule.view.tab.RouteListFragment
import com.samtakoj.schedule.view.tab.SmartFragmentStatePagerAdapter

class TypeTransportAdapter(fm: FragmentManager, val fragments: ArrayList<RouteListFragment>): SmartFragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): String {
        when (position) {
            0 -> return "bus"
            1 -> return "troll"
            2 -> return "tram"
            3 -> return "under"
            else -> return ""
        }
    }
}