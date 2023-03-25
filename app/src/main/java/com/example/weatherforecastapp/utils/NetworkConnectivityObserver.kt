package com.example.weatherforecastapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


interface NetworkStatusObserver {
    fun observeNetworkConnection(): Flow<MyNetworkStatus>
}

object NetworkConnectivityObserver : NetworkStatusObserver {
    private lateinit var connectivityManager: ConnectivityManager

    fun initNetworkConnectivity(context: Context) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun observeNetworkConnection(): Flow<MyNetworkStatus> {
        return callbackFlow {  // my producer is network status
            val callBack = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(MyNetworkStatus.Available) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(MyNetworkStatus.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(MyNetworkStatus.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(MyNetworkStatus.Unavailable) }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callBack)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callBack)
            }
        }.distinctUntilChanged()
    }


}