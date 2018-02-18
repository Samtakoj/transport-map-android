package com.samtakoj.schedule

import android.app.Application
import com.nytimes.android.external.fs3.SourcePersisterFactory
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import okio.BufferedSource
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import rx.Observable
import com.nytimes.android.external.store3.base.Persister
import com.samtakoj.schedule.api.Api
import com.samtakoj.schedule.data.RetrofitCsv
import com.samtakoj.schedule.data.TimeCsvParser
import com.samtakoj.schedule.model.RouteCsv
import com.samtakoj.schedule.model.StopCsv
import com.samtakoj.schedule.model.TimeCsv
import io.objectbox.BoxStore
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


/**
 * Created by Александр on 18.03.2017.
 */
class TransportApplication : Application() {

    private lateinit var persister: Persister<BufferedSource, BarCode>
    lateinit var persistedStopStore: Store<List<StopCsv>, BarCode>
    lateinit var persistedTimeStore: Store<List<TimeCsv>, BarCode>
    lateinit var persistedRouteStore: Store<List<RouteCsv>, BarCode>

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        boxStore = MyObjectBox.builder().androidContext(this).build()
        persister = SourcePersisterFactory.create(applicationContext.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS))
        persistedStopStore = providePersistedStore(StopCsv::class.java, true, ";")
        persistedRouteStore = providePersistedStore(RouteCsv::class.java, true, ";")

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

    companion object {

        private fun <T> fetcher(barCode: BarCode, clazz: Class<T>, skipHeader: Boolean, delimiter: String): Observable<BufferedSource> {
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
    }
}

