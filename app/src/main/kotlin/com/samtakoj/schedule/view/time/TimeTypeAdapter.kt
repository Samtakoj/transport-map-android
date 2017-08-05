package com.samtakoj.schedule.view.time

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.samtakoj.schedule.view.TimeListViewAdapter
import com.samtakoj.schedule.view.tab.SmartFragmentStatePagerAdapter
import com.samtakoj.shedule.model.WorkDay

class TimeTypeAdapter(fm: FragmentManager, val workDays: List<WorkDay>, val listAdapter: TimeListViewAdapter): SmartFragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return TimeListFragment.newInstance(listAdapter)
    }

    override fun getCount(): Int {
        return workDays.size
    }

    override fun getPageTitle(position: Int): String {
        val type = workDays[position].weekDay
        when (type) {
            "12345" -> return "Рабочие дни"
            "67" -> return "Выходные дни"
            "2345" -> return "Вторник - пятница"
            "1" -> return "Понедельник"
            "2" -> return "Вторник"
            "3" -> return "Среда"
            "4" -> return "Четверг"
            "5" -> return "Пятница"
            "6" -> return "Суббота"
            "7" -> return "Воскресенье"
            else -> return ""
        }
    }
}