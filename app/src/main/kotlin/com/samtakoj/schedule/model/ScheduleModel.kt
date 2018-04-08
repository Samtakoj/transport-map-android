package com.samtakoj.schedule.model

import com.samtakoj.schedule.data.LongListConverter
import com.samtakoj.schedule.data.WorkDayConverter
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

data class StopCsv1(var id: Int, var name: String, var lng: Long, var ltd: Long)
data class RouteCsv1(var num: String, var transportType: String, var name: String,
                    var weekDays: String, var id: Int, var stops: String)
data class TimeCsv1(val routeId: Int, val intervalCount: Int, val timeTable: List<Long>, val workDay: List<WorkDay1>)
data class WorkDay1(val weekDay: String, val countInterval: Int)
data class TestModel(val route: RouteCsv, val stops: List<StopCsv>, val times: TimeCsv)

data class WorkDay(val weekDay: String, val countInterval: Int) : Serializable
@Entity
data class TimeCsv(@Id var id: Long = 0, val routeId: Long = 0, val intervalCount: Int = 0,
                   @Convert(converter = LongListConverter::class, dbType = String::class) val timeTable: List<Long> = listOf(),
                   @Convert(converter = WorkDayConverter::class, dbType = String::class) val workDay: List<WorkDay> = listOf()) : Serializable
@Entity
data class StopCsv(@Id(assignable = true) var id: Long = 0, val name: String, val lng: Long, val ltd: Long) : Serializable
@Entity
data class RouteCsv(@Id(assignable = true) var id: Long = 0, var num: String, var transportType: String,
                    var name: String, var weekDays: String, var stops: String, var isFavorites: Boolean = false) : Serializable
