package com.samtakoj.schedule

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.samtakoj.schedule.api.ScheduleFetcher
import me.wangyuwei.particleview.ParticleView
import org.jetbrains.anko.find

/**
 * Created by artsiom.chuiko on 14/04/2017.
 */
class SplashActivity: AppCompatActivity(), RequestPermissionCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        val anim = find<ParticleView>(R.id.splash)
        ScheduleFetcher.loadIfEmpty(application as TransportApplication)
        anim.startAnim()
        anim.setOnParticleAnimListener {
            ApplicationPermissions.requestBasic(this, this)
        }
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