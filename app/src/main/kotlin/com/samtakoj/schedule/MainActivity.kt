package com.samtakoj.schedule

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ListView
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.*
import android.widget.AdapterView.OnItemClickListener
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.RouteCsv_
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.StopCsv_
import com.samtakoj.schedule.view.RouteListViewAdapter
import com.samtakoj.schedule.view.TypeTransportAdapter
import com.samtakoj.schedule.view.tab.RouteListFragment
import io.objectbox.Box
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onQueryTextListener
import org.jetbrains.anko.support.v4.viewPager

class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var routeList: Map<String, List<RouteCsv>>
    lateinit var allRoutes: Map<String, List<RouteCsv>>
    var type = "bus"
    var isFavorites = false
    lateinit var typesAdapter: TypeTransportAdapter
    lateinit var listView: ListView
    lateinit var routeBox: Box<RouteCsv>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        routeBox = (application as TransportApplication).boxStore.boxFor(RouteCsv::class.java)

        val location = SmartLocation.with(this).location().lastLocation // 53.89 27.54
        val verticalLayout = verticalLayout {
            appBarLayout {
                backgroundColor = Color.argb(255, 237, 149, 42)
                id = R.id.lunch_appbar

                toolbar {
                    id = R.id.lunch_toolbar
                    imageView {
                        imageResource = R.drawable.ar_icon
                        onClick {
                            startActivity<TestActivity>()
                        }
                    }.lparams {
                        gravity = Gravity.RIGHT
                        marginEnd = 10
                        width = sp(28)
                        height = sp(28)
                    }
                    imageView {
                        imageResource = R.drawable.star
                        onClick {
                            var imageResource = R.drawable.star

                            isFavorites = if(!isFavorites) {
                                routeList = allRoutes
                                true
                            } else {
                                routeList = getFavoritesRoutes()
                                imageResource = R.drawable.star_filled
                                false
                            }

                            setListToAdapter(routeList)
                            this@imageView.imageResource = imageResource
                        }
                    }.lparams {
                        gravity = Gravity.RIGHT
                        marginEnd = 20
                        width = sp(28)
                        height = sp(28)
                    }
                    searchView {
                        id = R.id.search_view
                        onQueryTextListener {
                            onQueryTextChange { newValue ->
                                routeList = if(!isFavorites) {
                                    allRoutes
                                } else {
                                    getFavoritesRoutes()
                                }
                                routeList = if (!TextUtils.isEmpty(newValue!!.trim())) {
                                    bg {
                                        getRoutesByInputValue(newValue)
                                    }.await()
                                } else {
                                    allRoutes
                                }

                                setListToAdapter(routeList)

                                true
                            }
                        }
                    }.lparams {
                        gravity = Gravity.RIGHT
                    }
                }
                tabLayout {
                    id = R.id.lunch_tabs
                    setTabTextColors(Color.LTGRAY, Color.WHITE)
                    setSelectedTabIndicatorColor(Color.WHITE)
                }
            }
            viewPager {
                id = R.id.lunch_pager_container
                backgroundColor = Color.WHITE
            }
        }

        allRoutes = routeBox.query().order(RouteCsv_.num).build().find().groupBy { route ->
            "${route.num}-${route.transportType}"
        }.flatMap { grouped ->
            grouped.value.take(1)
        }.groupBy { it.transportType }.mapValues {
            val transportType = it.key
            it.value.sortedBy {
                if(transportType != "metro") {
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
            }
        }

        routeList = allRoutes


        setSupportActionBar(find<Toolbar>(R.id.lunch_toolbar))
        viewPager = find<ViewPager>(R.id.lunch_pager_container)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(find<TabLayout>(R.id.lunch_tabs)))

        typesAdapter = TypeTransportAdapter(supportFragmentManager)

        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager?, f: android.support.v4.app.Fragment?, v: View?, savedInstanceState: Bundle?) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)

                if((f as RouteListFragment).name == "bus")
                    selectAdapterForActiveType(0)
            }
        }, false)

        viewPager.adapter = typesAdapter
        setupTabs(viewPager)
        viewPager.offscreenPageLimit = 4

        find<TabLayout>(R.id.lunch_tabs).addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectAdapterForActiveType(tab!!.position)
                find<Toolbar>(R.id.lunch_toolbar).backgroundColor = getColorByPosition(tab.position)
                find<TabLayout>(R.id.lunch_tabs).backgroundColor = getColorByPosition(tab.position)
            }

        })
    }

    private fun setListToAdapter(routeList: Map<String, List<RouteCsv>>) {
        for (index in 0 until 4) {
            val listAdapter = (typesAdapter.getRegisteredFragment(index) as RouteListFragment).listAdapter
            listAdapter.clear()
            listAdapter.addAll(routeList.getValue(getTransportType(index.toLong())))
        }
    }

    private fun getFavoritesRoutes(): Map<String, List<RouteCsv>> {
        val resultMap = mutableMapOf<String, List<RouteCsv>>()
        for (key in routeList.keys) {
            resultMap[key] = routeList[key]!!.filter {
                it.isFavorites
            }
        }
        return resultMap
    }

    private fun getRoutesByInputValue(value: String): Map<String, List<RouteCsv>> {

        return routeBox.query().order(RouteCsv_.num).build().find().groupBy { route ->
            "${route.num}-${route.transportType}"
        }.flatMap { grouped ->
            grouped.value.take(1)
        }.groupBy { it.transportType }.mapValues {
            val transportType = it.key
            it.value.sortedBy {
                if(transportType != "metro") {
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
            }.filter {
                        val inStops = isValueInStops(it, value)
                        val inName = it.name.toUpperCase().contains(Regex(value.toUpperCase()))
                        val isNumber = it.num.toUpperCase().contains(Regex(value.toUpperCase()))
                        inName || isNumber || inStops
            }
        }
    }

    private fun isValueInStops(route: RouteCsv, value: String): Boolean {
        val stopBox = (application as TransportApplication).boxStore.boxFor(StopCsv::class.java)
        val stopIds = route.stops.split(",").map {
            it.toLong()
        }.toLongArray()

        val stopsCount = stopBox.query()
                .`in`(StopCsv_.id, stopIds)
                .build()
                .find().filter {
                    it.name.toUpperCase().contains(Regex(value.toUpperCase()))
                }.size

        return stopsCount != 0
    }

    private fun setupTabs(viewPager: ViewPager?) {
        val tabLayout = find<TabLayout>(R.id.lunch_tabs)
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0 until tabLayout.tabCount) {
            val listAdapter = RouteListViewAdapter(this, routeList.getValue(getTransportType(i.toLong())).toMutableList())
            typesAdapter.addFragment(listAdapter, i)
            tabLayout.getTabAt(i)!!.setIcon(getPageIcon(i))
        }
    }

    private fun getRouteStops(route: RouteCsv): List<StopCsv?> {
        val stopBox = (application as TransportApplication).boxStore.boxFor(StopCsv::class.java)
        val stopIds = route.stops.split(",").map {
            it.toLong()
        }.toLongArray()

        return stopBox.query().`in`(StopCsv_.id, stopIds).sort { s1, s2 ->
            stopIds.indexOf(s1.id) - stopIds.indexOf(s2.id)
        }.build().find()
    }

    private fun selectAdapterForActiveType(typeIndex: Int) {
        type = getTransportType(typeIndex.toLong())

        listView = typesAdapter.getRegisteredFragment(typeIndex).view as ListView
        val listAdapter = listView.adapter as RouteListViewAdapter
        listAdapter.clear()
        listAdapter.addAll(routeList.getValue(type))

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            Log.i("TEST", "itemClick: position = $position, id = $id")

            val routeTo = routeList[type]!![position]
            val routeFrom = routeBox.query()
                    .equal(RouteCsv_.num, routeTo.num)
                    .and()
                    .equal(RouteCsv_.transportType, routeTo.transportType)
                    .and()
                    .notEqual(RouteCsv_.id, routeTo.id).build().findFirst()

            val stopsTo = getRouteStops(routeTo)
            val stopsFrom = getRouteStops(routeFrom!!)

            startActivity<StopsActivity>("stopsTo" to stopsTo, "routeTo" to routeTo, "routeFrom" to routeFrom, "stopsFrom" to stopsFrom, "isChange" to true)
        }
    }

    private fun getPageIcon(position: Int): Int {
        return when (position) {
            0 -> R.drawable.bus_icon
            1 -> R.drawable.troll_icon
            2 -> R.drawable.tram_icon
            3 -> R.drawable.under_icon
            else -> 1
        }
    }

    private fun getColorByPosition(position: Int): Int {
        return when(position) {
            0 -> Color.argb(255, 237, 149, 42)
            1 -> Color.argb(255, 79, 173, 101)
            3 -> Color.argb(255, 39, 59, 122)
            2 -> Color.argb(255, 244, 75, 63)
            else -> -1
        }
    }

    private fun getTransportType(position: Long): String {
        return when (position) {
            0L -> "bus"
            1L -> "trol"
            2L -> "tram"
            3L -> "metro"
            else -> ""
        }
    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        super.onDestroy()
    }
}
