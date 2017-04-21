package com.samtakoj.schedule

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.schedule.utils.StopUtils
import com.samtakoj.shedule.model.StopCsv
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by artsiom.chuiko on 14/04/2017.
 */
class SplashActivity: AppCompatActivity(), RequestPermissionCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stopBox = (application as TransportApplication).boxStore.boxFor(StopCsv::class.java)
        (application as TransportApplication).persistedStopStore.get(BarCode("Stop", "stops"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { stopBox.put(it.map(StopUtils::filler)) }

        ApplicationPermissions.requestBasic(this, this)
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

    override fun permissionsWereGranted() {
        startActivity(Intent(this, TestActivity::class.java))
        finish()
    }
}