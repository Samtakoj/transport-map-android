package com.samtakoj.schedule.data

import com.samtakoj.schedule.model.RouteCsv
import io.objectbox.BoxStore

/**
 * Created by artsiom.chuiko on 20/02/2018.
 */
class RoutesParser(private val store: BoxStore, skipHeaders: Boolean, regex: String)
    : CsvParser<RouteCsv>(skipHeaders, regex) {
    override fun defaultObject(): RouteCsv = RouteCsv(0, "", "", "", "", "")

    override fun createObject(prev: RouteCsv, vararg data: String): RouteCsv {
        val num = data[0]
        val transportType = data[3]
        val name = data[10]
        val weekDays = data[11]
        val id = data[12].toLong()
        val stops = data[14]
        return RouteCsv(
                id = if (id != 0L) id else prev.id,
                num = if (num.trim().isNotEmpty()) num else prev.num,
                transportType = if (transportType.trim().isNotEmpty()) transportType else prev.transportType,
                name = if (name.trim().isNotEmpty()) name else prev.name,
                weekDays = if (weekDays.trim().isNotEmpty()) weekDays else prev.weekDays,
                stops = if (stops.trim().isNotEmpty()) stops else prev.stops
        )
    }

    override fun afterParsing(parsed: List<RouteCsv>) {
        val routeBox = store.boxFor(RouteCsv::class.java)
        routeBox.put(parsed)
    }
}
