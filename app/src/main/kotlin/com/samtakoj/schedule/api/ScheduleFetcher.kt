package com.samtakoj.schedule.api

import android.util.Log
import com.nytimes.android.external.store.base.impl.BarCode
import com.samtakoj.schedule.TransportApplication
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TimeCsv
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*


/**
 * Created by Александр on 18.03.2017.
 */
object ScheduleFetcher {

    lateinit private var stops: Observable<StopCsv>
    lateinit private var routes: Observable<RouteCsv>
    lateinit private var times: Observable<TimeCsv>
    lateinit var subStop: Subscription

    fun test(app: TransportApplication)  {
//        val csvStops = app.persistedStopStore.get(BarCode("Stop", "stops"))
//        val csvRoutes = app.persistedRouteStore.get(BarCode("Route", "routes"))
//        val csvTimes = app.persistedTimeStore.get(BarCode("Time", "times"))
        subStop = app.persistedStopStore.get(BarCode("Stop", "stops"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { Observable.from(it) }
                .subscribe(object: Subscriber<StopCsv>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        // cast to retrofit.HttpException to get the response code
                        e.printStackTrace()
                        if (e is HttpException) {
                            val response: HttpException = e
                            val code = response.code()
                        }
                    }

                    override fun onNext(stop: StopCsv) {
                        Log.i("TRANSPORT_SCHEDULE", "Stop: ${stop.name}")
                    }
                })

//        stops = csvStops.flatMap { Observable.from(it)
//        }
//        routes = csvRoutes.flatMapIterable { routes ->
//            routes
//        }
//        times = csvTimes.flatMapIterable { times ->
//            times
//        }
//
//        var previousStop: StopCsv = StopCsv(1, "test", 123, 1234)
//        var previousRoute: RouteCsv = RouteCsv("1", "bus", "test", "1234567", 1, "1,2,3")
//        stops.map { stop ->
//            stop.id = if(stop.id == 0) previousStop.id else stop.id
//            stop.name = if(stop.name == "") previousStop.name else stop.name
//            stop.ltd = if(stop.ltd == 0.toLong()) previousStop.ltd else stop.ltd
//            stop.lng = if(stop.lng == 0.toLong()) previousStop.lng else stop.lng
//
//            previousStop = stop
//            return@map stop
//        }
//        routes.map { route ->
//            route.num = if(route.num == "") previousRoute.num else route.num
//            route.transportType = if(route.transportType == "") previousRoute.transportType else route.transportType
//            route.name = if(route.name == "") previousRoute.name else route.name
//            route.weekDays = if(route.weekDays == "") previousRoute.weekDays else route.weekDays
//            route.id = if(route.id == 0) previousRoute.id else route.id
//            route.stops = if(route.stops == "") previousRoute.stops else route.stops
//
//            previousRoute = route
//            return@map route
//        }

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