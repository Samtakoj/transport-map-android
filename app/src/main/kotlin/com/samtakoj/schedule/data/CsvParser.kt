package com.samtakoj.schedule.data

import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.util.stream.Collectors

/**
 * Created by artsiom.chuiko on 20/02/2018.
 */
interface Parser<T> {
    fun defaultObject(): T
    fun createObject(prev: T, vararg data: String): T
    fun afterParsing(parsed: List<T>)
}

abstract class CsvParser<T>(private val skipHeaders: Boolean, private val regex: String)
    : Converter.Factory(), Converter<ResponseBody, List<T>>, Parser<T> {

    override fun convert(value: ResponseBody): List<T> {
        val parsed = convertSourceToList(value.source(), skipHeaders, regex)
        afterParsing(parsed)
        return parsed
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *> {
        return this
    }

    override fun afterParsing(parsed: List<T>) {
    }

    private fun convertSourceToList(source: BufferedSource, skipHeaders: Boolean, regex: String): List<T> {
        BufferedReader(InputStreamReader(source.inputStream(), Charset.forName("UTF-8"))).use { reader ->
            var previous = defaultObject()
            return reader.lines().skip(if(skipHeaders) 1 else 0)
                    .map {
                        previous = createObject(previous, *it.split(regex).toTypedArray())
                        previous
                    }
                    .collect(Collectors.toList())
        }
    }
}
