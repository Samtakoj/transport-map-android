package com.samtakoj.schedule.utils

import com.samtakoj.shedule.model.RouteCsv
import com.samtakoj.shedule.model.StopCsv

/**
 * Created by artsiom.chuiko on 21/04/2017.
 */

object StopUtils {
    var dummyStop = StopCsv(0, "", 0, 0)
    inline fun filler(stop: StopCsv): StopCsv {
        stop.name = if (stop.name != "") stop.name else dummyStop.name
        stop.id = if (stop.id != 0L) stop.id else dummyStop.id
        stop.lng = if (stop.lng != 0L) stop.lng else dummyStop.lng
        stop.ltd = if (stop.ltd != 0L) stop.ltd else dummyStop.ltd
        dummyStop = stop
        return stop
    }
}

object RouteUtils {
    var dummyRoute = RouteCsv("", "", "", "", 1, "")
    fun filler(route: RouteCsv): RouteCsv {
        route.id = if (route.id != 0L) route.id else dummyRoute.id
        route.name = if (route.name.trim() != "") route.name else dummyRoute.name
        route.num = if (route.num.trim() != "") route.num else dummyRoute.num
        route.stops = if (route.stops.trim() != "") route.stops else dummyRoute.stops
        route.transportType = if (route.transportType.trim() != "") route.transportType else dummyRoute.transportType
        route.weekDays = if (route.weekDays.trim() != "") route.weekDays else dummyRoute.weekDays
        dummyRoute = route
        return route
    }
}