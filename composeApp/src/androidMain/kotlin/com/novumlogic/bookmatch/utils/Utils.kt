package com.novumlogic.bookmatch.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

object Utils {
    fun isNetworkAvailable(context: Context): Boolean{
        val isConnected: Boolean
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        isConnected = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return isConnected
    }

}

fun Context.observeConnectionAsFlow()= callbackFlow {
    val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager

    val callback = NetworkCallback { connectionState -> trySend(connectionState) }
    val networkRequest = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()

    connectivityManager.registerNetworkCallback(networkRequest,callback)
    val currentState = getCurrentConnectivityState(connectivityManager)
    trySend(currentState)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current

    return produceState(initialValue = context.currentConnectivityState) {
        context.observeConnectionAsFlow().collect{ value = it}
    }

}

fun NetworkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionState.Available)
        }

        override fun onLost(network: Network) {
            callback(ConnectionState.Unavailable)
        }
    }
}

val Context.currentConnectivityState: ConnectionState
    get(){
        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }

fun getCurrentConnectivityState(connectivityManager: ConnectivityManager): ConnectionState{
    val connected = connectivityManager.allNetworks.any {
        connectivityManager.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
    return if(connected) ConnectionState.Available else ConnectionState.Unavailable
}

sealed class ConnectionState{
    object Available: ConnectionState()
    object Unavailable: ConnectionState()
}