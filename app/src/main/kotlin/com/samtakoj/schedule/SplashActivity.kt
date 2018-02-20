package com.samtakoj.schedule

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.samtakoj.schedule.extensions.transportApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.setContentView

/**
 * Created by artsiom.chuiko on 14/04/2017.
 */
class SplashActivity: AppCompatActivity(), RequestPermissionCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transportApp().stopApi.fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {error ->
                    Log.e(BuildConfig.APPLICATION_ID, "Error while fetching stops: ${error.message}")
                })
        transportApp().timeApi.fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {error ->
                    Log.e(BuildConfig.APPLICATION_ID, "Error while fetching times: ${error.message}")
                })
        transportApp().routeApi.fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {error ->
                    Log.e(BuildConfig.APPLICATION_ID, "Error while fetching routes: ${error.message}")
                })

        SplashActivityUi().setContentView(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ApplicationPermissions.INITIAL_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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