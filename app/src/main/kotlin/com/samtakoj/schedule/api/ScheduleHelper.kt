package com.samtakoj.schedule.api

import android.app.Activity
import com.samtakoj.schedule.extensions.transportApp
import com.samtakoj.schedule.model.*
import io.nlopez.smartlocation.SmartLocation
import java.util.*

/**
 * Created by alex on 4/24/18.
 */
class ScheduleHelper(private val activity: Activity) {

    private val location = SmartLocation.with(activity).location().lastLocation
    private var currentHours = 0
    private var currentMinutes = 0

    companion object {
        private const val EARTH_RADIUS_KM = 6384.0

        fun calculateDistanceMeters(aLong: Double, aLat: Double, bLong: Double, bLat: Double): Double {
            val d2r = Math.PI / 180

            val dLat = (bLat - aLat) * d2r
            val dLon = (bLong - aLong) * d2r
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(aLat * d2r) * Math.cos(bLat * d2r)
                    * Math.sin(dLon / 2) * Math.sin(dLon / 2))
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

            return this.EARTH_RADIUS_KM * c * 1000.0

        }
    }

    fun getInfoForRoute(route: RouteCsv): String {
        val stopIds = route.stops.split(",").map { it.toLong() }.toLongArray()
        val nearStops = getNearStops(stopIds)

        if(nearStops.count() == 0) {
            return ""
        }

        val timeBox = activity.transportApp().boxStore.boxFor(TimeCsv::class.java)
        val times = timeBox.query().equal(TimeCsv_.routeId, route.id).build().findFirst()

        return getInfoString(nearStops, stopIds, times!!)
    }

    fun getInfoForStop(stop: StopCsv, routeId: Long, stopPosition: Int): String {
        val distance = calculateDistanceMeters(location!!.longitude, location.latitude, stop.lng * 0.00001, stop.ltd * 0.00001)

        if(distance > 500) {
            return ""
        }

        val timeBox = activity.transportApp().boxStore.boxFor(TimeCsv::class.java)
        val times = timeBox.query().equal(TimeCsv_.routeId, routeId).build().findFirst()

        return getInfoString(stopPosition, times!!)
    }

    private fun getCurrentTime(): Int {
        val calendar = Calendar.getInstance()

        currentMinutes = calendar.get(Calendar.MINUTE)
        currentHours = calendar.get(Calendar.HOUR_OF_DAY)
        currentHours = if(currentHours == 0) {
            24
        } else {
            currentHours
        }

        return currentHours * 60 + currentMinutes
    }

    private fun getNearStops(stopIds: LongArray): List<StopCsv> {
        val stopBox = activity.transportApp().boxStore.boxFor(StopCsv::class.java)

        return stopBox.query().`in`(StopCsv_.id, stopIds).filter {
            calculateDistanceMeters(location!!.longitude, location.latitude, it.lng * 0.00001, it.ltd * 0.00001) <= 500
        }.build().find()
    }

    private fun getInfoString(nearStops: List<StopCsv>, stopIds: LongArray, times: TimeCsv): String {
        var currFuture = 10000
        var currStop = nearStops[0]
        val currentTime = getCurrentTime()
        val currentWeakDay = getWeakDay()

        val workDay = times.workDay.first {
            it.weekDay.contains(Regex(currentWeakDay))
        }
        val workDayPosition = times.workDay.indexOf(workDay)

        nearStops.forEach {
            val stopPosition = stopIds.indexOf(it.id)
            val skipCount = times.intervalCount * stopPosition + workDayPosition * workDay.countInterval

            val timeIntervals = times.timeTable.subList(skipCount, skipCount + workDay.countInterval)


            if(timeIntervals.max()!! >= currentTime) {
                val nearFuture = timeIntervals.first { it >= currentTime } - currentTime

                if(nearFuture <= currFuture) {
                    currFuture = nearFuture.toInt()
                    currStop = it
                }
            }
        }

        return if(currFuture == 10000) {
            "only tomorrow ${currStop.name}"
        } else {
            "~$currFuture ${currStop.name}"
        }
    }

    private fun getInfoString(stopPosition: Int, times: TimeCsv): String {
        val currentTime = getCurrentTime()
        val currentWeakDay = getWeakDay()

        val workDay = times.workDay.first {
            it.weekDay.contains(Regex(currentWeakDay))
        }
        val workDayPosition = times.workDay.indexOf(workDay)

        val skipCount = times.intervalCount * stopPosition + workDayPosition * workDay.countInterval
        val timeIntervals = times.timeTable.subList(skipCount, skipCount + workDay.countInterval)

        if(timeIntervals.max()!! < currentTime) {
            return "only tomorrow"
        }

        val nearFuture = timeIntervals.first { it >= currentTime } - currentTime
        return "~$nearFuture minutes"
    }

    private fun getWeakDay(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        return if(day == 1) {
            7
        } else {
            day - 1
        }.toString()
    }
}