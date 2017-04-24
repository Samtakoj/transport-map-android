package com.samtakoj.schedule

import android.app.ActionBar
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.*
import android.widget.AdapterView.OnItemClickListener
import com.samtakoj.schedule.view.RouteListViewAdapter
import com.samtakoj.schedule.view.TypeTransportAdapter
import com.samtakoj.shedule.model.*
import io.objectbox.Box
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.onTabSelectedListener
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.support.v4.viewPager
import java.lang.Long.parseLong


class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var routeList: Map<String, List<RouteCsv>>
    var type = "bus"
    lateinit var typesAdapter: TypeTransportAdapter
    lateinit var listView: ListView
    lateinit var routeBox: Box<RouteCsv>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val location = SmartLocation.with(this).location().lastLocation
        val verticalLayout = verticalLayout {
            appBarLayout {
                backgroundColor = Color.argb(255, 99, 196, 207)
                id = R.id.lunch_appbar

                toolbar {
                    id = R.id.lunch_toolbar
                    textView {
                        lparams {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        textSize = sp(12).toFloat()
                        text = "Transport MINSK"
                        textColor = Color.argb(255, 255, 255, 255)
                    }
                }

                tabLayout {
                    id = R.id.lunch_tabs
                    setTabTextColors(Color.WHITE, Color.GREEN)
                    setSelectedTabIndicatorColor(Color.GREEN)
                }
            }
            viewPager {
                id = R.id.lunch_pager_container
                backgroundColor = Color.WHITE
            }
        }

        routeBox = (application as TransportApplication).boxStore.boxFor(RouteCsv::class.java)

        routeList = routeBox.query().order(RouteCsv_.num).build().find().groupBy { route ->
            "${route.num}-${route.transportType}"
        }.flatMap { grouped ->
            grouped.value.take(1)
        }.groupBy { it.transportType }

//        setSupportActionBar(find<Toolbar>(R.id.lunch_toolbar))
        viewPager = find<ViewPager>(R.id.lunch_pager_container)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(find<TabLayout>(R.id.lunch_tabs)))

        val listAdapter = RouteListViewAdapter(this, ArrayList<RouteCsv>())
        listAdapter.clear()
        listAdapter.addAll(routeList.getValue(type).sortedBy {
            if(type != "metro") {
                var char = it.num.last()
                var counter = 0
                while (!"0123456789".contains(char)) {
                    counter++
                    char = it.num.substring(0, it.num.length - counter).last()
                }
                it.num.substring(0, it.num.length - counter).toInt()
            } else {
                it.num.substring(1).toInt()
            }
        })
        typesAdapter = TypeTransportAdapter(supportFragmentManager, listAdapter)
        viewPager.adapter = typesAdapter
        setupTabs(viewPager)
        find<TabLayout>(R.id.lunch_tabs).addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.i("LOL", "${tab?.position}")
                selectAdapterForActiveType(tab!!.position)
            }

        })

        SmartLocation.with(this).location().oneFix().start { location ->
            toast("Lat: ${location.latitude}, Long: ${location.longitude}")
        }
    }

    private fun setupTabs(viewPager: ViewPager?) {
        val tabLayout = find<TabLayout>(R.id.lunch_tabs)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun getRouteStops(route: RouteCsv): List<StopCsv> {
        val stopBox = (application as TransportApplication).boxStore.boxFor(StopCsv::class.java)
        val stops = ArrayList<StopCsv>()
        route.stops.split(",").forEach { stopId ->
            stops.add(stopBox.query().equal(StopCsv_.id, stopId.toLong()).build().findFirst())
        }

        return stops
    }

    fun selectAdapterForActiveType(typeIndex: Int) {
        type = getPageTitle(typeIndex.toLong())
        typesAdapter.listAdapter.clear()

        typesAdapter.listAdapter.addAll(routeList.getValue(type).sortedBy {
            if(type != "metro") {
                var char = it.num.last()
                var counter = 0
                while (!"0123456789".contains(char)) {
                    counter++
                    char = it.num.substring(0, it.num.length - counter).last()
                }
                it.num.substring(0, it.num.length - counter).toInt()
            } else {
                it.num.substring(1).toInt()
            }
        })


        listView = typesAdapter.getRegisteredFragment(typeIndex).view as ListView

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            Log.i("TEST", "itemClick: position = $position, id = $id")

            val routeTo = routeList[type]!![position]
            val routeFrom = routeBox.query()
                    .equal(RouteCsv_.num, routeTo.num)
                    .and()
                    .equal(RouteCsv_.transportType, routeTo.transportType)
                    .and()
                    .notEqual(RouteCsv_.id, routeTo.id).build().findFirst()

            Log.i("TRANSPORT_SCHEDULE", "RouteName: ${routeTo.name}")

            val stopsTo = getRouteStops(routeTo)
            val stopsFrom = getRouteStops(routeFrom)

            startActivity<StopsActivity>("stopsTo" to stopsTo, "routeTo" to routeTo, "routeFrom" to routeFrom, "stopsFrom" to stopsFrom)
        }
    }

//    override fun onNothingSelected(parent: AdapterView<*>?) {
//        selectAdapterForActiveType(0)
//    }
//
//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        Log.i("LOL", "test")
//    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        super.onDestroy()
    }

    fun getPageTitle(position: Long): String {
        when (position) {
            0L -> return "bus"
            1L -> return "trol"
            2L -> return "tram"
            3L -> return "metro"
            else -> return ""
        }
    }
}
