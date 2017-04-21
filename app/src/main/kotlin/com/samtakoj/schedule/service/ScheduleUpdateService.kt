package com.samtakoj.schedule.service

import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams
import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.schedule.TransportApplication
import com.samtakoj.shedule.model.RouteCsv
import com.samtakoj.shedule.model.StopCsv
import com.samtakoj.shedule.model.TimeCsv
import rx.Observer
import rx.android.schedulers.AndroidSchedulers

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
                .subscribeOn(AndroidSchedulers.mainThread())
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