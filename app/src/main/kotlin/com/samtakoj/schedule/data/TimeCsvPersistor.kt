package com.samtakoj.schedule.data

import android.util.Log
import com.samtakoj.schedule.model.TimeCsv
import com.samtakoj.schedule.model.WorkDay
import io.objectbox.BoxStore
import okhttp3.ResponseBody
import okio.BufferedSource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * Created by Александр on 25.03.2017.
 */
class TimeCsvPersistor(private val store: BoxStore) : CsvParser<TimeCsv>(false, "") {
    override fun defaultObject(): TimeCsv = TODO("not implemented")
    override fun createObject(prev: TimeCsv, vararg data: String): TimeCsv = TODO("not implemented")

    override fun convert(value: ResponseBody): List<TimeCsv> {
        return convertToTimeCsv(value?.source())
    }

    override fun afterParsing(parsed: List<TimeCsv>) {
        val timeBox = store.boxFor(TimeCsv::class.java)
        timeBox.put(parsed)
    }

    private fun convertToTimeCsv(t: BufferedSource?): List<TimeCsv> {
        val start = System.currentTimeMillis()
        BufferedReader(InputStreamReader(t?.inputStream(), Charset.forName("UTF-8"))).use { reader ->
            val parsed = mutableListOf<TimeCsv>()
            reader.lineSequence().forEach { line ->
                parsed.add(parseToTimeCsv(line))
            }
            Log.i("TRANSPORT_SCHEDULE", "Parse time = ${System.currentTimeMillis() - start}")
            this.afterParsing(parsed)
            return parsed
        }
    }

    private fun parseToTimeCsv(line: String): TimeCsv {
        val firstCommaIndex: Int = line.indexOf(",")
        var routeIdString: String = line.substring(0, firstCommaIndex)
        var routeId: Long
        try {
            routeId = routeIdString.toLong()
        } catch (e: Exception) {
            routeIdString = line.substring(1, firstCommaIndex)
            routeId = routeIdString.toLong()
        }

        val dataString: String = line.substring(firstCommaIndex + 1)
        val blocks: List<String> = dataString.split(delimiters = ",,", ignoreCase = false, limit =  5)
        val timesData: List<String> = blocks[0].split(delimiters = ",")
        val maxIndex: Int = timesData.count()
        val timeTable = getTimeTable(timesData, blocks[4])
        val workDays = getWorkDay(blocks[3], maxIndex)

        return TimeCsv(0, routeId, maxIndex, timeTable, workDays)
    }

    private fun getTimeTable(timesData: List<String>, intervals: String) : List<Long> {
        val timesDataLength = timesData.count()

        val timetable = mutableListOf<Long>()
        var previousTime: Long = 0
        timetable.addAll(timesData.map {
            previousTime += it.toInt()
            previousTime
        })

        for (intervalBlocks in intervals.split(",,")) {
            if (intervalBlocks.isEmpty()) {
                continue
            }

            val deltas = intervalBlocks.split(",")
            var delta: Int = 0
            var index: Int = 0

            var repeatTimes = 0
            var skipCount = 0
            while (index < deltas.count()) {

                delta = if(index == 0) deltas[index++].toInt() else delta + deltas[index++].toInt() - 5

                if(deltas.count() > 1)
                    repeatTimes = if(index >= deltas.count()) timesDataLength - skipCount else deltas[index++].toInt()
                else
                    repeatTimes = timesDataLength
                for(i in 1..repeatTimes) {
                    timetable.add(timetable[timetable.count() - timesDataLength] + delta)
                }
                skipCount += repeatTimes
            }
        }

        return timetable
    }

    private fun getWorkDay(dataString: String, totalCount: Int): List<WorkDay> {
        val tokens = dataString.split(",")

        val result = mutableListOf<WorkDay>()
        var countInterval = if(tokens.count() > 2) tokens[1].toInt() else 0
        var sum = 0
        for ((index, token) in tokens.withIndex()) {
            if (index % 2 == 0) {
                countInterval = if(index != tokens.count() - 1) tokens[index + 1].toInt() else 0
                result.add(WorkDay(token, if (index == tokens.count() - 1) totalCount - sum else countInterval))
                sum += countInterval
            }
        }

        return result
    }
}