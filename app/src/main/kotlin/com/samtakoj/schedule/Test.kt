package com.samtakoj.schedule

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.*
import android.widget.TextView
import android.widget.Toast
import com.beyondar.android.fragment.BeyondarFragmentSupport
import com.beyondar.android.view.BeyondarViewAdapter
import com.beyondar.android.view.OnClickBeyondarObjectListener
import com.beyondar.android.world.BeyondarObject
import com.beyondar.android.world.GeoObject
import com.beyondar.android.world.World
import io.nlopez.smartlocation.SmartLocation
import com.google.android.gms.location.DetectedActivity
import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.shedule.model.StopCsv_
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import io.nlopez.smartlocation.location.providers.MultiFallbackProvider
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by Александр on 11.03.2017.
 */

class TestActivity : AppCompatActivity(){

    companion object {
        lateinit var textView1: TextView
        var previous = com.samtakoj.shedule.model.StopCsv(1, "", 1, 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Logger.DEBUG_OPENGL = true

        MainActivityUi().setContentView(this)

        val toolbar = find<Toolbar>(MainActivityUi.ToolbarID)

        SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.activity_main)
                .inject()

        val startTime = System.currentTimeMillis()
        val stopBox = (application as TransportApplication).boxStore.boxFor(com.samtakoj.shedule.model.StopCsv::class.java)
        val list = stopBox.query().order(StopCsv_.id).build().find()
        Toast.makeText(this, "Count: ${list.size}, time: ${System.currentTimeMillis() - startTime}", Toast.LENGTH_LONG).show()

        val lat = find<TextView>(R.id.lat)
        val lng = find<TextView>(R.id.lng)

        val location = SmartLocation.with(this).location().lastLocation
        lat.text = "%.2f".format(location?.latitude)
        lng.text = "%.2f".format(location?.longitude)

        val fragment = supportFragmentManager.findFragmentByTag("TestFragment") ?: BeyondarFragmentSupport()
        supportFragmentManager.beginTransaction().replace(MainActivityUi.ContainerID, fragment, "TestFragment").commit()

        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager?, f: Fragment?, v: View?, savedInstanceState: Bundle?) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)

                val playServicesProvider = LocationGooglePlayServicesProvider()
                playServicesProvider.setCheckLocationSettings(true)
                playServicesProvider.setLocationSettingsAlwaysShow(true)
                val provider = MultiFallbackProvider.Builder().
                        withProvider(playServicesProvider).
                        withProvider(LocationManagerProvider()).
                        build()
                SmartLocation.with(this@TestActivity).location(provider).start { location ->
//                    val myLocation = Location(location)
//                    myLocation.latitude = 53.928738
//                    myLocation.longitude = 27.587694
                    val world = World(this@TestActivity)
                    world.setLocation(location)
                    world.setDefaultImage(R.drawable.flymer)
                    (application as TransportApplication).persistedStopStore.get(BarCode("Stop", "stops"))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap { Observable.from(it) }
                            .map({ stop ->
                                stop.name = if (stop.name != "") stop.name else previous.name
                                stop.id = if (stop.id != 0L) stop.id else previous.id
                                stop.lng = if (stop.lng != 0L) stop.lng else previous.lng
                                stop.ltd = if (stop.ltd != 0L) stop.ltd else previous.ltd
                                previous = stop
                                return@map stop
                            }).map { stop ->
                        val obj = GeoObject(stop.id.toLong())
                        obj.setGeoPosition(stop.ltd * 0.00001, stop.lng * 0.00001)
                        obj.setImageResource(R.drawable.goal)
                        obj.name = stop.name
                        return@map obj
                    }.subscribe(object: Subscriber<GeoObject>() {
                        override fun onCompleted() {
                            (fragment as BeyondarFragmentSupport).world = world
                            fragment.showFPS(true)
                            val customBeyondarViewAdapter = CustomBeyondarViewAdapter(this@TestActivity)
                            fragment.setOnClickBeyondarObjectListener(customBeyondarViewAdapter)
                            fragment.setBeyondarViewAdapter(customBeyondarViewAdapter)
                            fragment.maxDistanceToRender = 800f
                            fragment.distanceFactor = 30f
//                            fragment.pushAwayDistance = 80f
//                            LowPassFilter.ALPHA = 0.1f
                            Log.i("SCHEDULE", "World size: ${world.beyondarObjectLists[0].size()}")
                        }
                        override fun onNext(t: GeoObject?) {
                            world.addBeyondarObject(t)
                        }
                        override fun onError(e: Throwable?) {
                            e?.printStackTrace()
                        }
                    })
                }
            }
        }, true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ApplicationPermissions.INITIAL_REQUEST -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val playServicesProvider = LocationGooglePlayServicesProvider()
                    playServicesProvider.setCheckLocationSettings(true)
                    playServicesProvider.setLocationSettingsAlwaysShow(true)
                    val provider = MultiFallbackProvider.Builder().
                            withProvider(playServicesProvider).
                            withProvider(LocationManagerProvider()).
                            build()


                    SmartLocation.with(this).
                            location(provider).
                            start { location ->
                                //textView1.text = "Lat: ${location?.latitude}, Lng: ${location?.longitude}"
                            }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun onDestroy() {
        SmartLocation.with(this).location().stop()
        SmartLocation.with(this).activity().stop()
        super.onDestroy()
    }

    class CustomBeyondarViewAdapter(context: Context): BeyondarViewAdapter(context), OnClickBeyondarObjectListener {
        val showViewOn = ArrayList<BeyondarObject>()
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun onClickBeyondarObject(beyondarObjects: ArrayList<BeyondarObject>?) {
            if (beyondarObjects?.size == 0) return
            val beyondarObject = beyondarObjects?.get(0)
            if (showViewOn.contains(beyondarObject)) {
                showViewOn.remove(beyondarObject)
            } else {
                showViewOn.add(beyondarObject as BeyondarObject)
            }
        }

        override fun getView(beyondarObject: BeyondarObject?, recycledView: View?, parent: ViewGroup?): View? {
            if (!showViewOn.contains(beyondarObject)) {
                return Util.nullHack()
            }

            var view = recycledView
            if (recycledView == null) {
                view = inflater.inflate(R.layout.object_view, null)
            }

            val textView = view?.findViewById(R.id.info) as TextView
            textView.text = "${beyondarObject?.name} -> ${"%.0f".format(beyondarObject?.distanceFromUser)}m"

            setPosition(beyondarObject?.screenPositionTopRight)

            return view
        }
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
            when (activityType.type) {
                DetectedActivity.IN_VEHICLE -> return "in vehicle"
                DetectedActivity.ON_BICYCLE -> return "on bicycle"
                DetectedActivity.ON_FOOT -> return "on foot"
                DetectedActivity.STILL -> return "still"
                DetectedActivity.TILTING -> return "tilting"
                else -> return "unknown"
            }
        }
    }
}