package com.samtakoj.schedule

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.*
import android.widget.TextView
import com.beyondar.android.fragment.BeyondarFragmentSupport
import com.beyondar.android.opengl.util.LowPassFilter
import com.beyondar.android.world.GeoObject
import com.beyondar.android.world.World
import io.nlopez.smartlocation.SmartLocation
import com.google.android.gms.location.DetectedActivity
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.StopCsv_
import com.samtakoj.schedule.view.CustomBeyondarViewAdapter
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import io.nlopez.smartlocation.location.providers.MultiFallbackProvider
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Александр on 11.03.2017.
 */

class TestActivity : AppCompatActivity(){

    companion object {
        lateinit var textView1: TextView
        var previous = StopCsv(1, "", 1, 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainActivityUi().setContentView(this)

        val toolbar = find<Toolbar>(MainActivityUi.ToolbarID)

        SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.activity_main)
                .inject()

        val lat = find<TextView>(R.id.lat)
        val lng = find<TextView>(R.id.lng)

        val location = SmartLocation.with(this).location().lastLocation
        lat.text = "%.2f".format(location?.latitude)
        lng.text = "%.2f".format(location?.longitude)

        val fragment = supportFragmentManager.findFragmentByTag("TestFragment") ?: BeyondarFragmentSupport()
        supportFragmentManager.beginTransaction().replace(MainActivityUi.ContainerID, fragment, "TestFragment").commit()

        val playServicesProvider = LocationGooglePlayServicesProvider()
        playServicesProvider.setCheckLocationSettings(true)
        playServicesProvider.setLocationSettingsAlwaysShow(true)
        val provider = MultiFallbackProvider.Builder().
                withProvider(playServicesProvider).
                withProvider(LocationManagerProvider()).
                build()

        val world = World(this@TestActivity)
        SmartLocation.with(this@TestActivity).location(provider).config(LocationParams.BEST_EFFORT)
                .start { location ->
//                    location.latitude = 53.930
//                    location.longitude = 27.588
            world.setLocation(location)
            //fragment.showFPS(true)
        }

        if (location?.latitude != null) world.setLocation(location)
//        world.setDefaultImage(R.drawable.flymer)
        val stops = (application as TransportApplication).boxStore.boxFor(StopCsv::class.java).query().order(StopCsv_.id).build().find()
        Log.i("TEST", "$stops")
        stops.map { stop ->
            val obj = GeoObject(stop.id)
            obj.setGeoPosition(stop.ltd * 0.00001, stop.lng * 0.00001)
            obj.setImageResource(R.drawable.goal)
            obj.name = stop.name
            obj
        }.forEach { world.addBeyondarObject(it) }

        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager?, f: Fragment?, v: View?, savedInstanceState: Bundle?) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)

                (fragment as BeyondarFragmentSupport).world = world
                val customBeyondarViewAdapter = CustomBeyondarViewAdapter(this@TestActivity)
                fragment.setOnClickBeyondarObjectListener(customBeyondarViewAdapter)
                fragment.setBeyondarViewAdapter(customBeyondarViewAdapter)
                fragment.maxDistanceToRender = 400f
                fragment.distanceFactor = 25f
                // fragment.showFPS(true)

//                fragment.pullCloserDistance = 100f
//                fragment.pushAwayDistance = 50f
            }
        }, true)
    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        SmartLocation.with(this).activity().stop()
        super.onDestroy()
    }

    class TestFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return UI {
                frameLayout {
                    lparams(width = matchParent, height = matchParent)
                    padding = dip(24)
                    verticalLayout {
                        textView("Hello world!") {
                            id = MainActivityUi.StatusID
                            textSize = 24f
                        }
                        textView1 = textView("Null") {
                            id = MainActivityUi.LocationID
                            textSize = 24f
                        }
//                        space().lparams(width = wrapContent, height = dip(30))
                        button("Click") {
                            onClick {
                                startActivity<MainActivity>()
                            }
                        }
                    }
                }
            }.view
        }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val textView = view?.findViewById(MainActivityUi.StatusID) as TextView

            SmartLocation.with(activity).activity().start { activity ->
                textView.text = "Activity: ${getNameFromType(activity)}, confidence: ${activity?.confidence}"
            }
        }

        private fun getNameFromType(activityType: DetectedActivity): String {
            return when (activityType.type) {
                DetectedActivity.IN_VEHICLE -> "in vehicle"
                DetectedActivity.ON_BICYCLE -> "on bicycle"
                DetectedActivity.ON_FOOT -> "on foot"
                DetectedActivity.STILL -> "still"
                DetectedActivity.TILTING -> "tilting"
                else -> "unknown"
            }
        }
    }
}