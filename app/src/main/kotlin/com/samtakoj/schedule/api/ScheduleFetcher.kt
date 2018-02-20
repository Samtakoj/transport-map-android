package com.samtakoj.schedule.api

import com.nytimes.android.external.store3.base.impl.BarCode
import com.samtakoj.schedule.TransportApplication
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TestModel
import com.samtakoj.schedule.model.TimeCsv
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

/**
 * Created by Александр on 18.03.2017.
 */
object ScheduleFetcher {

//    lateinit private var stops: Observable<StopCsv>
//    lateinit private var routes: Observable<RouteCsv>
//    lateinit private var times: Observable<TimeCsv>
//
//    fun loadIfEmpty(app: TransportApplication) {
//        val stopBox = app.boxStore.boxFor(StopCsv::class.java)
//        val routeBox = app.boxStore.boxFor(RouteCsv::class.java)
//        val timeBox = app.boxStore.boxFor(TimeCsv::class.java)
//
//        val isEmpty = stopBox.count() < 100L || routeBox.count() < 100L || timeBox.count() < 100L
//        if(isEmpty) {
//            var previousStop = StopCsv(1, "", 1, 1)
//            app.persistedStopStore.get(BarCode("Stop", "stops"))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe { stops ->
//                        val newStops = stops.map { stop ->
//                            stop.name = if (stop.name != "") stop.name else previousStop.name
//                            stop.id = if (stop.id != 0L) stop.id else previousStop.id
//                            stop.lng = if (stop.lng != 0L) stop.lng else previousStop.lng
//                            stop.ltd = if (stop.ltd != 0L) stop.ltd else previousStop.ltd
//                            previousStop = stop
//                            stop
//                        }
//                        stopBox.put(newStops)
//                    }
//
//            var previousRoute = RouteCsv(0, "", "", "", "", "")
//            app.persistedRouteStore.get(BarCode("Route", "routes"))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe { routes ->
//                        val newRoutes = routes.map { route ->
//                            route.id = if (route.id != 0L) route.id else previousRoute.id
//                            route.name = if (route.name != null && route.name.trim() != "") route.name else previousRoute.name
//                            route.num = if (route.num != null && route.num.trim() != "") route.num else previousRoute.num
//                            route.stops = if (route.stops != null && route.stops.trim() != "") route.stops else previousRoute.stops
//                            route.transportType = if (route.transportType != null && route.transportType.trim() != "") route.transportType else previousRoute.transportType
//                            route.weekDays = if (route.weekDays != null && route.weekDays.trim() != "") route.weekDays else previousRoute.weekDays
//                            previousRoute = route
//                            route
//                        }.groupBy { route ->
//                            "${route.num}-${route.transportType}"
//                        }.flatMap { grouped ->
//                            grouped.value.take(2)
//                        }
//                        routeBox.put(newRoutes)
//                    }
//
//            app.persistedTimeStore.get(BarCode("Time", "times"))
//                    .toObservable()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(timeBox::put)
//        }
//    }

//    fun stops (app: TransportApplication): Observable<StopCsv> {
//        var previous = StopCsv(1, "", 1, 1)
//        return app.persistedStopStore.get(BarCode("Stop", "stops"))
//                .toObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap { Observable.fromIterable(it) }
//                .map({ stop ->
//                    stop.name = if (stop.name.trim() != "") stop.name else previous.name
//                    stop.id = if (stop.id != 0L) stop.id else previous.id
//                    stop.lng = if (stop.lng != 0L) stop.lng else previous.lng
//                    stop.ltd = if (stop.ltd != 0L) stop.ltd else previous.ltd
//                    previous = stop
//                    return@map stop
//                })
//    }

//    fun routes (app: TransportApplication): Observable<RouteCsv> {
//        var previous = RouteCsv(0, "", "", "", "", "")
//        var counter = 0
//        return app.persistedRouteStore.get(BarCode("Route", "routes"))
//                .toObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap { Observable.fromIterable(it) }
//                .map({ route ->
//                    route.id = if (route.id != 0L) route.id else previous.id
//                    route.name = if (route.name.trim() != "") route.name else previous.name
//                    route.num = if (route.num.trim() != "") route.num else previous.num
//                    route.stops = if (route.stops.trim() != "") route.stops else previous.stops
//                    route.transportType = if (route.transportType.trim() != "") route.transportType else previous.transportType
//                    route.weekDays = if (route.weekDays.trim() != "") route.weekDays else previous.weekDays
//                    previous = route
//                    return@map route
//                })
//                .groupBy { route ->
//                    return@groupBy "${route.num}-${route.transportType}"
//                }
//                .flatMap { grouped ->
//                    counter = 0
//                    return@flatMap grouped
//                }
//                .filter {
//                    counter++
//                    counter <= 2
//                }
//    }

//    fun times (app: TransportApplication, routeId: Long): Observable<TimeCsv> {
//        return app.persistedTimeStore.get(BarCode("Time", "times"))
//                .toObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap { Observable.fromIterable(it) }
//                .filter { time ->
//                    time.routeId == routeId
//                }
//    }

//    fun getAllTimes (app: TransportApplication): Observable<TimeCsv> {
//        return app.persistedTimeStore.get(BarCode("Time", "times"))
//                .toObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap { Observable.fromIterable(it) }
//    }

//    fun getStops(app: TransportApplication, route: RouteCsv): Observable<TestModel> {
//        val stopArr = route.stops.split(",")
//        val times = getAllTimes(app).filter { time -> time.routeId == route.id }
//        return Observable.zip(stops(app)
//                .filter { stop ->
//                     stopArr.contains(stop.id.toString())
//                }
//                .toSortedList { stop1, stop2 ->
//                    val index1 = stopArr.indexOf(stop1.id.toString())
//                    val index2 = stopArr.indexOf(stop2.id.toString())
//                    when {
//                        index1 < index2 -> return@toSortedList -1
//                        index1 > index2 -> return@toSortedList 1
//                        else -> return@toSortedList 0
//                    }
//                }.toObservable(), times, BiFunction { stops, times -> TestModel(route, stops, times) })
//    }
//
//    fun getList(app: TransportApplication): Observable<TestModel> {
//        return routes(app).flatMap { route ->
//            getStops(app, route)
//        }.groupBy { testModel ->
//            return@groupBy "${testModel.route.num}-${testModel.route.transportType}"
//        }.flatMap { grouped ->
//            return@flatMap grouped
//        }
//    }

}