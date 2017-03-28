package com.samtakoj.schedule.model

import com.samtakoj.schedule.data.RetrofitCsv.Position

data class StopCsv(@Position(1) var id: Int, @Position(4) var name: String, @Position(6) var lng: Long, @Position(7) var ltd: Long)
data class RouteCsv(@Position(1) var num: String, @Position(4) var transportType: String, @Position(11) var name: String,
                    @Position(12) var weekDays: String, @Position(13) var id: Int, @Position(15) var stops: String)
data class TimeCsv(val routeId: Int, val intervalCount: Int, val timeTable: List<Long>, val workDay: List<WorkDay>)
data class WorkDay(val weekDay: String, val countInterval: Int)