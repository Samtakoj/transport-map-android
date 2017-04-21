package com.samtakoj.schedule.model

import com.samtakoj.shedule.model.RouteCsv
import com.samtakoj.shedule.model.StopCsv
import com.samtakoj.shedule.model.TimeCsv

data class StopCsv1(var id: Int, var name: String, var lng: Long, var ltd: Long)
data class RouteCsv1(var num: String, var transportType: String, var name: String,
                    var weekDays: String, var id: Int, var stops: String)
data class TimeCsv1(val routeId: Int, val intervalCount: Int, val timeTable: List<Long>, val workDay: List<WorkDay1>)
data class WorkDay1(val weekDay: String, val countInterval: Int)
data class TestModel(val route: RouteCsv, val stops: List<StopCsv>, val times: TimeCsv)
