package com.samtakoj.schedule.api

import com.samtakoj.schedule.BuildConfig
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TimeCsv
import io.reactivex.Observable
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET

/**
 * Created by Александр on 23.03.2017.
 */
interface Api {
    companion object {
        inline fun <reified T : Api> provideRetrofit(noinline parserSupplier: () -> Converter.Factory) : T {
            return Retrofit.Builder()
                    .baseUrl("http://minsktrans.by/")
                    .addConverterFactory(parserSupplier())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .validateEagerly(BuildConfig.DEBUG)
                    .build()
                    .create(T::class.java)
        }
    }
}

interface TimeApi : Api {

    @GET("/city/minsk/times.txt")
    fun fetch(): Observable<List<TimeCsv>>
}

interface StopsApi : Api {

    @GET("/city/minsk/stops.txt")
    fun fetch(): Observable<List<StopCsv>>
}

interface RoutesApi : Api {

    @GET("/city/minsk/routes.txt")
    fun fetch(): Observable<List<RouteCsv>>
}