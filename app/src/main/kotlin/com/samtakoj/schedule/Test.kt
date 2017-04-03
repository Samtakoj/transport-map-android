package com.samtakoj.schedule

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*
import android.widget.TextView
import com.beyondar.android.fragment.BeyondarFragmentSupport
import com.beyondar.android.world.GeoObject
import com.beyondar.android.world.World
import io.nlopez.smartlocation.SmartLocation
import com.google.android.gms.location.DetectedActivity
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import io.nlopez.smartlocation.location.providers.LocationManagerProvider
import io.nlopez.smartlocation.location.providers.MultiFallbackProvider

/**
 * Created by Александр on 11.03.2017.
 */

class TestActivity : AppCompatActivity() {

    companion object {
        lateinit var textView1: TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationPermissions.requestBasic(this)
        MainActivityUi().setContentView(this)

        val toolbar = find<Toolbar>(MainActivityUi.ToolbarID)

        SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.activity_main)
                .inject()

        val lat = find<TextView>(R.id.lat)
        val lng = find<TextView>(R.id.lng)

        val location = SmartLocation.with(this).location().lastLocation
        lat.text = "%.2f".format(location?.latitude)
        lng.text = "%.2f".format(location?.longitude)

        val world = World(this)
        world.setDefaultImage(R.mipmap.ic_launcher)
        world.setLocation(location)

        val obj1 = GeoObject(3)
        obj1.setLocation(location)
        obj1.imageUri = "http://beyondar.com/sites/default/files/logo_reduced.png"
        obj1.name = "Name"

        val obj2 = GeoObject(4)
        obj2.setGeoPosition(41.26518966360719, 1.92582424468222)
        obj2.imageUri = "http://beyondar.com/sites/default/files/logo_reduced.png"
        obj2.name = "Name1"

        val obj3 = GeoObject(5)
        obj3.setGeoPosition(41.26550959641445, 1.925873388087619)
        obj3.setImageResource(R.drawable.flymer)
        obj3.name = "Name2"

        val obj4 = GeoObject(6)
        obj4.setGeoPosition(41.26518862002349, 1.925662767707665)
        obj4.setImageResource(R.drawable.flymer)
        obj4.name = "Name3"

        world.addBeyondarObject(obj1)
        world.addBeyondarObject(obj2)
        world.addBeyondarObject(obj3)
        world.addBeyondarObject(obj4)

        val fragment = supportFragmentManager.findFragmentByTag("TestFragment")?: createBeyondARFragment(this)
        supportFragmentManager.beginTransaction().replace(MainActivityUi.ContainerID, fragment, "TestFragment").commit()

        supportFragmentManager.registerFragmentLifecycleCallbacks(object: android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks(){
            override fun onFragmentViewCreated(fm: android.support.v4.app.FragmentManager?, f: android.support.v4.app.Fragment?, v: View?, savedInstanceState: Bundle?) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                (fragment as BeyondarFragmentSupport).world = world
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

    private fun createBeyondARFragment(activity: Activity): BeyondarFragmentSupport {
        val fragment = BeyondarFragmentSupport()
        return fragment
    }

    class TestFragment: Fragment() {
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
                        space().lparams(width = wrapContent, height = dip(30))
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