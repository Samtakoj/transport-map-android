package com.samtakoj.schedule.model

import com.samtakoj.schedule.data.RetrofitCsv.Position

data class StopCsv(@param:Position(1) var id: Int, @param:Position(5) var name: String, @param:Position(7) var lng: Long, @param:Position(8) var ltd: Long)
data class RouteCsv(@field:Position(1) var num: String, @field:Position(4) var transportType: String, @field:Position(11) var name: String,
                    @field:Position(12) var weekDays: String, @field:Position(13) var id: Int, @field:Position(15) var stops: String)
data class TimeCsv(val routeId: Int, val intervalCount: Int, val timeTable: List<Long>, val workDay: List<WorkDay>)
data class WorkDay(val weekDay: String, val countInterval: Int)