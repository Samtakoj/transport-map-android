package com.samtakoj.schedule.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by Александр on 23.03.2017.
 */
interface Api {

    @GET("/city/minsk/{dataType}.txt")
    fun fetchData(@Path("dataType") dataType: String): Single<ResponseBody>

}