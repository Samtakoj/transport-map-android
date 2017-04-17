package com.samtakoj.schedule.model

import com.samtakoj.schedule.data.RetrofitCsv.Position

data class StopCsv(@param:Position(1) var id: Int, @param:Position(5) var name: String, @param:Position(7) var lng: Long, @param:Position(8) var ltd: Long)
data class RouteCsv(@param:Position(1) var num: String, @param:Position(4) var transportType: String, @param:Position(11) var name: String,
                    @param:Position(12) var weekDays: String, @param:Position(13) var id: Int, @param:Position(15) var stops: String)
data class TimeCsv(val routeId: Int, val intervalCount: Int, val timeTable: List<Long>, val workDay: List<WorkDay>)
data class WorkDay(val weekDay: String, val countInterval: Int)
data class TestModel(val route: RouteCsv, val stops: List<StopCsv>, val times: TimeCsv)