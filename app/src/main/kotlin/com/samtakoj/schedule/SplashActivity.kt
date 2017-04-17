package com.samtakoj.schedule

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.shedule.model.StopCsv
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by artsiom.chuiko on 14/04/2017.
 */
class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stopBox = (application as TransportApplication).boxStore.boxFor(com.samtakoj.shedule.model.StopCsv::class.java)
        var previous = com.samtakoj.shedule.model.StopCsv(1, "", 1, 1)
        (application as TransportApplication).persistedStopStore.get(BarCode("Stop", "stops"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { stops ->
                    stops.map{ stop ->
                        stop.name = if (stop.name != "") stop.name else previous.name
                        stop.id = if (stop.id != 0L) stop.id else previous.id
                        stop.lng = if (stop.lng != 0L) stop.lng else previous.lng
                        stop.ltd = if (stop.ltd != 0L) stop.ltd else previous.ltd
                        previous = stop
                        return@map stop
                    }
                    stopBox.put(stops)
                }

        ApplicationPermissions.requestBasicOrStart(this, Intent(this, TestActivity::class.java))

        //SplashActivityUi().setContentView(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ApplicationPermissions.INITIAL_REQUEST -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, TestActivity::class.java))
                }
                finish()
            }
        }
    }
}