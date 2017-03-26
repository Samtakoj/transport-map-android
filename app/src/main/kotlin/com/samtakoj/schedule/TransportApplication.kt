package com.samtakoj.schedule

import android.app.Application
import com.nytimes.android.external.fs.SourcePersisterFactory
import com.nytimes.android.external.store.base.impl.BarCode
import com.nytimes.android.external.store.base.impl.Store
import okio.BufferedSource
import com.nytimes.android.external.store.base.impl.StoreBuilder
import rx.Observable
import com.nytimes.android.external.store.base.Persister
import com.samtakoj.schedule.api.Api
import com.samtakoj.schedule.data.RetrofitCsv
import com.samtakoj.schedule.data.TimeCsvParser
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TimeCsv
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.RxJavaCallAdapterFactory


/**
 * Created by Александр on 18.03.2017.
 */
class TransportApplication : Application() {

    var persistedStopStore: Store<List<StopCsv>, BarCode> = null!!
    var persistedTimeStore: Store<List<TimeCsv>, BarCode> = null!!
    var persistedRouteStore: Store<List<RouteCsv>, BarCode> = null!!
    private var persister: Persister<BufferedSource, BarCode> = null!!

    override fun onCreate() {
        persister = SourcePersisterFactory.create(applicationContext.cacheDir)
        persistedStopStore = providePersistedStore(StopCsv::class.java, true, ";")
        persistedRouteStore = providePersistedStore(RouteCsv::class.java, true, ";")

        val parser = TimeCsvParser()
        persistedTimeStore = StoreBuilder.parsedWithKey<BarCode, BufferedSource, List<TimeCsv>>()
                .fetcher({barCode ->
                    Retrofit.Builder()
                        .baseUrl("http://minsktrans.by/")
                        .addConverterFactory(parser)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .validateEagerly(BuildConfig.DEBUG)
                        .build()
                        .create(Api::class.java)
                        .fetchData(barCode.key).map(ResponseBody::source)
                })
                .persister(persister)
                .parser(parser)
                .open()
    }

    private fun <T> providePersistedStore(clazz: Class<T>, skipHeader: Boolean, delimiter: String): Store<List<T>, BarCode> {
        return StoreBuilder.parsedWithKey<BarCode, BufferedSource, List<T>>()
                .fetcher({barCode -> fetcher(barCode, clazz, skipHeader, delimiter)})
                .persister(persister)
                .parser(RetrofitCsv.createSourceParser(clazz, skipHeader,  delimiter))
                .open()
    }

    private fun <T> fetcher(barCode: BarCode, clazz: Class<T>, skipHeader: Boolean, delimiter: String): Observable<BufferedSource> {
        return provideRetrofit(clazz, skipHeader, delimiter).fetchData(barCode.key).map(ResponseBody::source)
    }

    private fun <T> provideRetrofit(clazz: Class<T>, skipHeader: Boolean, delimiter: String) : Api {
        return  Retrofit.Builder()
                .baseUrl("http://minsktrans.by/")
                .addConverterFactory(RetrofitCsv.createConverterFactory(clazz, skipHeader, delimiter))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .validateEagerly(BuildConfig.DEBUG)
                .build()
                .create(Api::class.java)
    }

}

