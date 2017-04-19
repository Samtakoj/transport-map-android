package com.samtakoj.schedule.api

import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.schedule.TransportApplication
import com.samtakoj.schedule.model.TestModel
import com.samtakoj.shedule.model.RouteCsv
import com.samtakoj.shedule.model.StopCsv
import com.samtakoj.shedule.model.TimeCsv
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func2
import rx.schedulers.Schedulers

/**
 * Created by Александр on 18.03.2017.
 */
object ScheduleFetcher {

    lateinit private var stops: Observable<StopCsv>
    lateinit private var routes: Observable<RouteCsv>
    lateinit private var times: Observable<TimeCsv>

    fun stops (app: TransportApplication): Observable<StopCsv>  {
        var previous = StopCsv(1, "", 1, 1)
        return app.persistedStopStore.get(BarCode("Stop", "stops"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .map({ stop ->
                    stop.name = if (stop.name.trim() != "") stop.name else previous.name
                    stop.id = if (stop.id != 0L) stop.id else previous.id
                    stop.lng = if (stop.lng != 0L) stop.lng else previous.lng
                    stop.ltd = if (stop.ltd != 0L) stop.ltd else previous.ltd
                    previous = stop
                    return@map stop
                })
    }

    fun routes (app: TransportApplication): Observable<RouteCsv> {
        var previous = RouteCsv("", "", "", "", 1, "")
        var counter = 0
        return app.persistedRouteStore.get(BarCode("Route", "routes"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .map({ route ->
                    route.id = if (route.id != 0L) route.id else previous.id
                    route.name = if (route.name.trim() != "") route.name else previous.name
                    route.num = if (route.num.trim() != "") route.num else previous.num
                    route.stops = if (route.stops.trim() != "") route.stops else previous.stops
                    route.transportType = if (route.transportType.trim() != "") route.transportType else previous.transportType
                    route.weekDays = if (route.weekDays.trim() != "") route.weekDays else previous.weekDays
                    previous = route
                    return@map route
                })
                .groupBy { route ->
                    return@groupBy "${route.num}-${route.transportType}"
                }
                .flatMap { grouped ->
                    counter = 0
                    return@flatMap grouped
                }
                .filter {
                    counter++
                    counter <= 2
                }
    }

    fun times (app: TransportApplication, routeId: Long): Observable<TimeCsv> {
        return app.persistedTimeStore.get(BarCode("Time", "times"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .filter { time ->
                    time.routeId == routeId
                }
    }

    fun getAllTimes (app: TransportApplication): Observable<TimeCsv> {
        return app.persistedTimeStore.get(BarCode("Time", "times"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
    }

    fun getStops(app: TransportApplication, route: RouteCsv): Observable<TestModel> {
        val stopArr = route.stops.split(",")
        val times = getAllTimes(app).filter { time -> time.routeId == route.id }
        return Observable.zip(stops(app)
                .filter { stop ->
                     stopArr.contains(stop.id.toString())
                }
                .toSortedList { stop1, stop2 ->
                    val index1 = stopArr.indexOf(stop1.id.toString())
                    val index2 = stopArr.indexOf(stop2.id.toString())
                    when {
                        index1 < index2 -> return@toSortedList -1
                        index1 > index2 -> return@toSortedList 1
                        else -> return@toSortedList 0
                    }
                }, times, Func2 { stops, times -> TestModel(route, stops, times) })
    }

    fun getList(app: TransportApplication): Observable<TestModel> {
        return routes(app).flatMap { route ->
            getStops(app, route)
        }.groupBy { testModel ->
            return@groupBy "${testModel.route.num}-${testModel.route.transportType}"
        }.flatMap { grouped ->
            return@flatMap grouped
        }
    }

}