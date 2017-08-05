package com.samtakoj.schedule

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.samtakoj.schedule.view.RouteListViewAdapter
import com.samtakoj.schedule.view.TimeListViewAdapter
import com.samtakoj.schedule.view.TypeTransportAdapter
import com.samtakoj.schedule.view.tab.RouteListFragment
import com.samtakoj.schedule.view.time.TimeListFragment
import com.samtakoj.schedule.view.time.TimeTypeAdapter
import com.samtakoj.shedule.model.*
import io.objectbox.Box
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.viewPager


class TimeActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var typesAdapter: TimeTypeAdapter
    lateinit var times: List<MutableMap<Long, List<Long>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val time = intent.extras.getSerializable("time") as TimeCsv
        val stopPosition = intent.extras.getInt("stopPosition")
        val stopName = intent.extras.getString("stopName")

        Log.i("TIMES_SCHEDULE", "Stop TIME: ${time.workDay}")

        val test = ArrayList<StopsActivity.MyTestModel>()
        var skipCount = time.intervalCount * stopPosition
        time.workDay.forEach {
            test.add(StopsActivity.MyTestModel(it, time.timeTable.subList(skipCount, skipCount + it.countInterval)))
            skipCount += it.countInterval
        }

        verticalLayout {
            toolbar {
                backgroundColor = Color.argb(255, 99, 196, 207)
                textView {
                    textColor = Color.WHITE
                    text = stopName
                }
            }
            tabLayout {
                backgroundColor = Color.argb(255, 99, 196, 207)
                id = R.id.lunch_tabs
                setTabTextColors(Color.LTGRAY, Color.WHITE)
                setSelectedTabIndicatorColor(Color.WHITE)
            }
            viewPager {
                id = R.id.lunch_pager_container
                backgroundColor = Color.WHITE
            }
        }

        times = test.map {
            it.timeTable.groupBy( { it / 60 }, { it % 60} ).toMutableMap()
        }

        viewPager = find<ViewPager>(R.id.lunch_pager_container)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(find<TabLayout>(R.id.lunch_tabs)))

        typesAdapter = createAdapter(time)
        viewPager.adapter = typesAdapter
        setupTabs(viewPager)

    }

    private fun createAdapter(time: TimeCsv) : TimeTypeAdapter {
        val fragments = arrayListOf<TimeListFragment>()
        for(i in 0..time.workDay.size - 1) {
            val listAdapter = TimeListViewAdapter(this, emptyMap<Long, List<Long>>().toMutableMap())
            listAdapter.clear()
            listAdapter.addAll(times[i])
            fragments.add(TimeListFragment.newInstance(listAdapter))
        }

        return TimeTypeAdapter(supportFragmentManager, time.workDay, fragments)
    }

    private fun setupTabs(viewPager: ViewPager?) {
        val tabLayout = find<TabLayout>(R.id.lunch_tabs)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}