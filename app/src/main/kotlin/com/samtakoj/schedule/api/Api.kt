package com.samtakoj.schedule.api

import com.samtakoj.schedule.BuildConfig
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Александр on 23.03.2017.
 */
interface Api {

    @GET("/city/minsk/{dataType}.txt")
    fun fetchData(@Path("dataType") dataType: String): Single<ResponseBody>

    companion object {

        fun provideRetrofit(parserSupplier: () -> Converter.Factory) : Api {
            return  Retrofit.Builder()
                    .baseUrl("http://minsktrans.by/")
                    .addConverterFactory(parserSupplier())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .validateEagerly(BuildConfig.DEBUG)
                    .build()
                    .create(Api::class.java)
        }
    }
}