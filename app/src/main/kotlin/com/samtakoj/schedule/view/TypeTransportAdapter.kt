package com.samtakoj.schedule.view

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import com.samtakoj.schedule.view.tab.RouteListFragment

class TypeTransportAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    private val registeredFragments = SparseArray<Fragment>()


    override fun getItem(position: Int): Fragment {
        return registeredFragments[position]
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

    fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position)
    }

    fun addFragment(listAdapter: RouteListViewAdapter, position: Int) {
        val newFragment = RouteListFragment.newInstance(listAdapter, getPageTitle(position))
        registeredFragments.put(position, newFragment)
    }
}