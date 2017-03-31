package com.samtakoj.schedule.api

import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.schedule.TransportApplication
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TimeCsv
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Александр on 18.03.2017.
 */
object ScheduleFetcher {

    lateinit private var stops: Observable<StopCsv>
    lateinit private var routes: Observable<RouteCsv>
    lateinit private var times: Observable<TimeCsv>
    //        val csvStops = app.persistedStopStore.get(BarCode("Stop", "stops"))
    //        val csvRoutes = app.persistedRouteStore.get(BarCode("Route", "routes"))
    //        val csvTimes = app.persistedTimeStore.get(BarCode("Time", "times"))
    fun stops (app: TransportApplication, subscriber: Subscriber<StopCsv>): Subscription  {
        return app.persistedStopStore.get(BarCode("Stop", "stops"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .subscribe(subscriber)
    }

    fun routes (app: TransportApplication, subscriber: Subscriber<RouteCsv>): Subscription {
        return app.persistedRouteStore.get(BarCode("Route", "routes"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .subscribe(subscriber)
    }

    fun times (app: TransportApplication, subscriber: Subscriber<TimeCsv>): Subscription {
        return app.persistedTimeStore.get(BarCode("Time", "times"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .subscribe(subscriber)
    }

    fun getStops(): Observable<StopCsv> {
        return stops
    }

    fun getFirstStop(): StopCsv {
        return stops.toBlocking().first()
    }

    fun getRoutes(): Observable<RouteCsv> {
        return routes
    }

    fun getTimes(): Observable<TimeCsv> {
        return times
    }

}