package com.novumlogic.bookmatch.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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


@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
): Modifier {

    val color = MaterialTheme.colorScheme.tertiary

    return drawWithContent {
        drawContent()

        val itemsCount = state.layoutInfo.totalItemsCount


        if (itemsCount > 0) {
            val firstIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index?.toFloat() ?: 0f
            val lastIndex = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index?.toFloat() ?: 0f

            val scrollbarTop = (firstIndex) / itemsCount * size.height
            val scrollBottom = (lastIndex + 1f) / itemsCount * size.height - 10
            val scrollbarHeight = scrollBottom - scrollbarTop


            drawRect(
                color = color,
                topLeft = Offset(size.width - width.toPx() - 1, scrollbarTop),
                size = Size(width.toPx(), scrollbarHeight)
            )
        }
    }
}



fun Long.formatToDate() : String{
    val instant = Instant.ofEpochMilli(this)
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    return instant.atZone(ZoneId.systemDefault()).format(formatter)
}

fun Long.formatToTime() : String{
    val time = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
    return time.format(DateTimeFormatter.ofPattern("hh:mm:ss a"))
}

fun Long.convertToStartOfDayMillis(): Long{
    //Convert to instant
    val instant = Instant.ofEpochMilli(this)

    //Extract LocalDate
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()

    //Create LocalDateTime for the start of the day
    val startOfDay = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)

    //Convert LocalDateTime back to Instant, considering the timezone
    val startOfDayInstant = startOfDay.atZone(ZoneId.systemDefault()).toInstant()

    //Extract milliseconds since epoch for the start of day
    return startOfDayInstant.toEpochMilli()
}

