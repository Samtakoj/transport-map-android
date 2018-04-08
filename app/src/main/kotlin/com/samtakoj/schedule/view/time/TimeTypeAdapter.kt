package com.samtakoj.schedule.view.time

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.samtakoj.schedule.model.WorkDay
import com.samtakoj.schedule.view.TimeListViewAdapter
import com.samtakoj.schedule.view.tab.SmartFragmentStatePagerAdapter

class TimeTypeAdapter(fm: FragmentManager, private val workDays: List<WorkDay>, var listAdapter: TimeListViewAdapter): SmartFragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
//        val newAdapter = TimeListViewAdapter(context, listAdapter.getAll())
        return TimeListFragment.newInstance(listAdapter)
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
}