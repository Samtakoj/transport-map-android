package com.samtakoj.schedule.model

import com.samtakoj.schedule.data.RetrofitCsv.Position

data class StopCsv(@Position(1) val id: Int, @Position(4) val name: String, @Position(6) val lng: Long, @Position(7) val ltd: Long)
data class RouteCsv(@Position(1) val num: String, @Position(4) val transportType: String, @Position(11) val name: String,
                    @Position(12) val weekDays: String, @Position(13) val id: Int, @Position(15) val stops: String)
data class TimeCsv(val routeId: Int, val intervalCount: Int, val timeTable: List<Long>, val workDay: List<WorkDay>)
data class WorkDay(val weekDay: String, val countInterval: Int)