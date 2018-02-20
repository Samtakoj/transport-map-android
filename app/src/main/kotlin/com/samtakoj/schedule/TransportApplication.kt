package com.samtakoj.schedule

import android.app.Application
import com.samtakoj.schedule.api.Api
import com.samtakoj.schedule.api.RoutesApi
import com.samtakoj.schedule.api.StopsApi
import com.samtakoj.schedule.api.TimeApi
import com.samtakoj.schedule.data.RoutesParser
import com.samtakoj.schedule.data.StopsParser
import com.samtakoj.schedule.data.TimeCsvParser
import com.samtakoj.schedule.model.MyObjectBox
import io.objectbox.BoxStore


/**
 * Created by Александр on 18.03.2017.
 */
class TransportApplication : Application() {

    lateinit var boxStore: BoxStore
    lateinit var timeApi: TimeApi
    lateinit var stopApi: StopsApi
    lateinit var routeApi: RoutesApi

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
        timeApi = Api.provideRetrofit({ TimeCsvParser(boxStore) })
        stopApi = Api.provideRetrofit({ StopsParser(boxStore, true, ";") })
        routeApi = Api.provideRetrofit({ RoutesParser(boxStore, true, ";") })
    }
}
