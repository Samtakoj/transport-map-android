package com.samtakoj.schedule.data

import com.samtakoj.schedule.model.StopCsv
import io.objectbox.BoxStore

/**
 * Created by artsiom.chuiko on 20/02/2018.
 */
class StopsParser(private val store: BoxStore, skipHeaders: Boolean, regex: String) : CsvParser<StopCsv>(skipHeaders, regex) {
    override fun defaultObject(): StopCsv = StopCsv(0, "", 0, 0)

    override fun createObject(prev: StopCsv, vararg data: String): StopCsv {
        val id = data[0].toLong()
        val name = data[4]
        val lng = data[6].toLong()
        val ltd = data[7].toLong()
        return StopCsv(
                id = if (id != 0L) id else prev.id,
                name = if (name.trim().isNotEmpty()) name else prev.name,
                lng = if (lng != 0L) lng else prev.lng,
                ltd = if (ltd != 0L) ltd else prev.ltd
        )
    }

    override fun afterParsing(parsed: List<StopCsv>) {
        val stopBox = store.boxFor(StopCsv::class.java)
        stopBox.put(parsed)
    }
}
