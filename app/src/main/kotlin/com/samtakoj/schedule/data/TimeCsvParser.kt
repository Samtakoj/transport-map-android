package com.samtakoj.schedule.data

import com.google.common.collect.Lists
import com.nytimes.android.external.store.base.Parser
import com.samtakoj.schedule.model.TimeCsv
import com.samtakoj.schedule.model.WorkDay
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * Created by Александр on 25.03.2017.
 */
class TimeCsvParser: Parser<BufferedSource, List<TimeCsv>>, Converter.Factory(), Converter<ResponseBody, List<TimeCsv>> {

    override fun call(t: BufferedSource?): List<TimeCsv> {
        return convertToTimeCsv(t)
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *> {
        return this
    }

    override fun convert(value: ResponseBody?): List<TimeCsv> {
        return convertToTimeCsv(value?.source())
    }

    private fun convertToTimeCsv(t: BufferedSource?): List<TimeCsv> {
        try {
            BufferedReader(InputStreamReader(t?.inputStream(), Charset.forName("UTF-8"))).use { reader ->
                var line: String
                val parsed = Lists.newArrayList<TimeCsv>()
                reader.lineSequence().forEach { line ->
                    parsed.add(parseToTimeCsv(line))
                }
                return parsed
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun parseToTimeCsv(line: String): TimeCsv {
        //TODO: parse line to TimeCsv object

        val firstCommaIndex: Int = line.indexOf(",")
        val routeIdString: String = line.substring(0, firstCommaIndex)
        val routeId: Int = routeIdString.toInt()

        val dataString: String = line.substring(firstCommaIndex + 1)

        // splitting data to 5 blocks:
        // 1 - times data
        // 2 - valid from
        // 3 - valid to
        // 4 - days of week
        // 5 - intervals
        val blocks: List<String> = dataString.split(delimiters = ",,", ignoreCase = false, limit =  5)
        val timesData: List<String> = blocks[0].split(delimiters = ",")
        val maxIndex: Int = timesData.count()

        val timeTable = getTimeTable(timesData, blocks[4])
        val workDays = getWorkDay(blocks[3], maxIndex)
//        val parsedBlock = blocks[3].split(delimiters = ",")
//        val countIntervalInWeekDays = Lists.newArrayList<Int>()
//        val weekDays = Lists.newArrayList<String>()
//        parsedBlock.withIndex().forEach { (i, str) ->
//            if (i % 2 == 0) {
//                weekDays.add(str)
//            } else {
//                countIntervalInWeekDays.add(str.toInt())
//            }
//        }


        return TimeCsv(routeId, maxIndex,timeTable, workDays)
    }

    private fun getTimeTable(timesData: List<String>, intervals: String) : List<Long>/*List<List<Long>>*/ {
        val timesDataLength = timesData.count()

//        val timetable = listOf(mutableListOf<Long>())
//        val delimiterWeekDay = Lists.newArrayList<Int>()
        val timetable = Lists.newArrayList<Long>()
        var previousTime: Long = 0
        var index: Int = 0
        for ((i, token) in timesData.withIndex()) {
//            if (token[0] == '-') {
//                delimiterWeekDay.add(i)
//            }
            previousTime += token.toInt()
            timetable.add(previousTime)
        }

        var j = 0
        for (intervalBlocks in intervals.split(",,")) {
            if (intervalBlocks.isEmpty()) {
                continue
            }

            val deltas = intervalBlocks.split(",")
            var delta: Int = 0
            var left: Int = 0
            index = 0
            while (index < deltas.count()) {
                if (left <= 0) {
                    delta = 5
                    left = timesDataLength
                }

                delta += deltas[index++].toInt() - 5

                val repeatTimes: Int = if (index == deltas.count()) left else delta
                left -= repeatTimes

                j = 0
                while (j < repeatTimes) {
                    timetable.add(timetable[timetable.count() - timesDataLength] + delta)
                    j++
                }
            }
        }

        return timetable
    }

    private fun getWorkDay(dataString: String, totalCount: Int): List<WorkDay> {
        val tokens = dataString.split(",")

        val result = Lists.newArrayList<WorkDay>()
        var countInterval = 0
        var sum = 0
        for ((index, token) in tokens.withIndex()) {
            if (index % 2 == 0) {
//                countInterval = if (index == tokens.count() - 1) totalCount - sum else countInterval
                result.add(WorkDay(token, if (index == tokens.count() - 1) totalCount - sum else countInterval))
            } else {
                countInterval = token.toInt()
                sum += countInterval
            }
        }

        return result
    }
}