package com.samtakoj.schedule

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.samtakoj.schedule.model.TimeCsv
import com.samtakoj.schedule.view.TimeListViewAdapter
import com.samtakoj.schedule.view.time.TimeTypeAdapter
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

        val test = ArrayList<StopsActivity.MyTestModel>()
        var skipCount = time.intervalCount * stopPosition
        time.workDay.forEach {
            test.add(StopsActivity.MyTestModel(it, time.timeTable.subList(skipCount, skipCount + it.countInterval)))
            skipCount += it.countInterval
        }

        verticalLayout {
            toolbar {
                backgroundColor = Color.argb(255, 79, 173, 101)
                textView {
                    textColor = Color.WHITE
                    text = stopName
                }
            }
            tabLayout {
                backgroundColor = Color.argb(255, 79, 173, 101)
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

        typesAdapter = TimeTypeAdapter(supportFragmentManager, time.workDay)
        viewPager.adapter = typesAdapter
        setupTabs(viewPager)
        viewPager.offscreenPageLimit = 7

    }

    private fun setupTabs(viewPager: ViewPager?) {
        val tabLayout = find<TabLayout>(R.id.lunch_tabs)
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0 until tabLayout.tabCount) {
            val listAdapter = TimeListViewAdapter(times[i])
            typesAdapter.addFragment(listAdapter, i)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}