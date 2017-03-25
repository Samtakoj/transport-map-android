package com.samtakoj.schedule.data

import com.google.common.collect.Lists
import com.nytimes.android.external.store.base.Parser
import com.samtakoj.schedule.model.TimeCsv
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
        return TimeCsv(1)
    }
}