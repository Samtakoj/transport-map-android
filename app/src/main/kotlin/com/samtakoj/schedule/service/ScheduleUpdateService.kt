package com.samtakoj.schedule.service

import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams
import com.nytimes.android.external.store3.base.impl.BarCode
import com.samtakoj.schedule.TransportApplication
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by artsiom.chuiko on 19/04/2017.
 */
class ScheduleUpdateService: GcmTaskService() {

    override fun onRunTask(p0: TaskParams?): Int {
        val app = application as TransportApplication
        val stopBox = app.boxStore.boxFor(StopCsv::class.java)
        val routeBox = app.boxStore.boxFor(RouteCsv::class.java)
        val timeBox = app.boxStore.boxFor(RouteCsv::class.java)

        app.persistedStopStore.get(BarCode("Stop", "stops"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { stops ->
                    if (stops.size > 0) {
                        stopBox.removeAll()
                        stopBox.put(stops)
                    }
                }
        return GcmNetworkManager.RESULT_SUCCESS
    }
}