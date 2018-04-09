package com.samtakoj.schedule.view.time

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import com.samtakoj.schedule.model.WorkDay
import com.samtakoj.schedule.view.TimeListViewAdapter

class TimeTypeAdapter(fm: FragmentManager, private val workDays: List<WorkDay>): FragmentStatePagerAdapter(fm) {
    private val registeredFragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment {
        return registeredFragments[position]
    }

    override fun getCount(): Int {
        return workDays.size
    }

    override fun getPageTitle(position: Int): String {
        val type = workDays[position].weekDay
        return when (type) {
            "12345" -> "Рабочие дни"
            "67" -> "Выходные дни"
            "2345" -> "Вторник - пятница"
            "1" -> "Понедельник"
            "2" -> "Вторник"
            "3" -> "Среда"
            "4" -> "Четверг"
            "5" -> "Пятница"
            "6" -> "Суббота"
            "7" -> "Воскресенье"
            else -> ""
        }
    }

    fun addFragment(listAdapter: TimeListViewAdapter, position: Int) {
        val newFragment = TimeListFragment.newInstance(listAdapter)
        registeredFragments.put(position, newFragment)
    }
}