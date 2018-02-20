package com.samtakoj.schedule

import android.app.Application
//import com.nytimes.android.external.fs3.SourcePersisterFactory
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import okio.BufferedSource
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.base.Persister
import com.samtakoj.schedule.api.Api
import com.samtakoj.schedule.data.RetrofitCsv
import com.samtakoj.schedule.data.TimeCsvParser
import com.samtakoj.schedule.model.MyObjectBox
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TimeCsv
import io.objectbox.BoxStore
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


/**
 * Created by Александр on 18.03.2017.
 */
class TransportApplication : Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
//        persistedStopStore = providePersistedStore(StopCsv::class.java, true, ";")
//        persistedRouteStore = providePersistedStore(RouteCsv::class.java, true, ";")

        val parser = TimeCsvParser()
        persistedTimeStore = StoreBuilder.parsedWithKey<BarCode, BufferedSource, List<TimeCsv>>()
                .fetcher({barCode ->
                    Retrofit.Builder()
                        .baseUrl("http://minsktrans.by/")
                        .addConverterFactory(parser)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .validateEagerly(BuildConfig.DEBUG)
                        .build()
                        .create(Api::class.java)
                        .fetchData(barCode.key).map(ResponseBody::source)
                })
//                .persister(persister)
                .parser(parser)
                .open()
    }

    companion object {

        private fun <T> fetcher(barCode: BarCode, clazz: Class<T>, skipHeader: Boolean, delimiter: String): Single<BufferedSource> {
            return provideRetrofit(clazz, skipHeader, delimiter).fetchData(barCode.key).map(ResponseBody::source)
        }

        private fun <T> provideRetrofit(clazz: Class<T>, skipHeader: Boolean, delimiter: String) : Api {
            return  Retrofit.Builder()
                    .baseUrl("http://minsktrans.by/")
                    .addConverterFactory(RetrofitCsv.createConverterFactory(clazz, skipHeader, delimiter))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .validateEagerly(BuildConfig.DEBUG)
                    .build()
                    .create(Api::class.java)
        }

        private fun <T> providePersistedStore(clazz: Class<T>, skipHeader: Boolean, delimiter: String): Store<List<T>, BarCode> {
            return StoreBuilder.parsedWithKey<BarCode, BufferedSource, List<T>>()
                    .fetcher({ barCode -> fetcher(barCode, clazz, skipHeader, delimiter) })
//                .persister(persister)
                    .parser(RetrofitCsv.createSourceParser(clazz, skipHeader,  delimiter))
                    .open()
        }
    }
}

