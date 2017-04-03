package com.samtakoj.schedule.api

import android.location.Location
import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.schedule.TransportApplication
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TimeCsv
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
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
    fun stops (app: TransportApplication, subscriber: Subscriber<StopCsv>, latitude: Double, longitude: Double): Subscription  {
        var previous = StopCsv(1, "", 1, 1)
        return app.persistedStopStore.get(BarCode("Stop", "stops"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .map({ stop ->
                    stop.name = if (stop.name != "") stop.name else previous.name
                    stop.id = if (stop.id != 0) stop.id else previous.id
                    stop.lng = if (stop.lng != 0.toLong()) stop.lng else previous.lng
                    stop.ltd = if (stop.ltd != 0.toLong()) stop.ltd else previous.ltd
                    previous = stop
                    return@map stop
                })
                .filter { (id, name, lng, ltd) ->
                    val dx = Math.abs(lng * 0.00001 - longitude) * 67.138
                    val dy = Math.abs(ltd * 0.00001 - latitude) * 111.321
                    return@filter Math.sqrt(dx * dx + dy * dy) <= 0.25
//                    ((Math.abs(lng * 0.00001 - longitude) <= 0.001) &&
//                            (Math.abs(ltd * 0.00001 - latitude) <= 0.001))
                }
                .subscribe(subscriber)
    }

    fun routes (app: TransportApplication, subscriber: Subscriber<RouteCsv>): Subscription {
        var previous = RouteCsv("", "", "", "", 1, "")
        return app.persistedRouteStore.get(BarCode("Route", "routes"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .map({ route ->
                    route.id = if (route.id != 0) route.id else previous.id
                    route.name = if (route.name != "") route.name else previous.name
                    route.num = if (route.num != "") route.num else previous.name
                    route.stops = if (route.stops != "") route.stops else previous.stops
                    route.transportType = if (route.transportType != "") route.transportType else previous.transportType
                    route.weekDays = if (route.weekDays != "") route.weekDays else previous.weekDays
                    previous = route
                    return@map route
                })
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