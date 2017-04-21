package com.samtakoj.schedule

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.schedule.api.ScheduleFetcher
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by artsiom.chuiko on 14/04/2017.
 */
class SplashActivity: AppCompatActivity(), RequestPermissionCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ScheduleFetcher.loadIfEmpty(application as TransportApplication)

        ApplicationPermissions.requestBasicOrStart(this, Intent(this, MainActivity::class.java))

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ApplicationPermissions.INITIAL_REQUEST -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            }
        }
    }

    override fun permissionsWereGranted() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}