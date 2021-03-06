package com.samtakoj.schedule

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by artsiom.chuiko on 27/03/2017.
 */
object ApplicationPermissions {
    val INITIAL_REQUEST = 1337

    val INITIAL = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    )

    fun requestBasic(activity: Activity, callback: RequestPermissionCallback?): Unit {
        when {
            !INITIAL.map { ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED }.fold(true) { init, res ->
                init && res
            } -> {
                ActivityCompat.requestPermissions(activity, INITIAL, INITIAL_REQUEST)
            }
            else -> {
                callback?.permissionsWereGranted()
            }
        }
    }
}

interface RequestPermissionCallback {
    fun permissionsWereGranted(): Unit
}