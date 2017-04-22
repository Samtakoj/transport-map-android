package com.samtakoj.schedule.api

import android.accounts.NetworkErrorException
import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by artsiom.chuiko on 22/04/2017.
 */
interface NetworkMonitor {
    fun isConnected(): Boolean
}

class LiveNetworkMonitor(context: Context): NetworkMonitor {
    val appContext = context.applicationContext!!

    override fun isConnected(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }
}

class NetworkMonitorInterceptor(val networkMonitor: NetworkMonitor): Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response? {
        when {
            networkMonitor.isConnected() -> return chain?.proceed(chain.request())
            else -> throw NetworkErrorException()
        }
    }
}