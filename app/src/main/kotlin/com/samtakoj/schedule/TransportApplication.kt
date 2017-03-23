package com.samtakoj.schedule

import android.app.Application
import com.nytimes.android.external.fs.SourcePersisterFactory
import com.nytimes.android.external.store.base.impl.BarCode
import com.nytimes.android.external.store.base.impl.Store
import com.samtakoj.schedule.model.Stop
import com.nytimes.android.external.store.middleware.GsonParserFactory
import okio.BufferedSource
import com.nytimes.android.external.store.base.impl.StoreBuilder
import rx.Observable
import com.nytimes.android.external.store.base.Persister
import com.samtakoj.schedule.api.Api
import com.samtakoj.schedule.data.RetrofitCsv
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.RxJavaCallAdapterFactory


/**
 * Created by Александр on 18.03.2017.
 */
class TransportApplication : Application() {

    private var persistedStore : Store<List<Stop>, BarCode> = null!!
    private var persister: Persister<BufferedSource, BarCode> = null!!

    override fun onCreate() {
        persister = SourcePersisterFactory.create(applicationContext.cacheDir)
        persistedStore = providePersistedRedditStore()
    }

    private fun providePersistedRedditStore(): Store<List<Stop>, BarCode> {
        return StoreBuilder.parsedWithKey<BarCode, BufferedSource, List<Stop>>()
                .fetcher(this::fetcher)
                .persister(persister)
                .parser(RetrofitCsv.createSourceParser(Stop::class.java, true,  ";"))
                .open()
    }


    private fun fetcher(barCode: BarCode): Observable<BufferedSource> {
        return provideRetrofit().fetchData(barCode.key).map(ResponseBody::source)
    }

    private fun provideRetrofit() : Api {
        return  Retrofit.Builder()
                .baseUrl("http://minsktrans.by/")
//                .addConverterFactory(/*GsonConverterFactory.create(provideGson())*/)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .validateEagerly(BuildConfig.DEBUG)  // Fail early: check Retrofit configuration at creation time in Debug build.
                .build()
                .create(Api::class.java)
    }

}

