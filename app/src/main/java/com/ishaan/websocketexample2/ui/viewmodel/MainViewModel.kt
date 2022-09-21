package com.ishaan.websocketexample2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.websocketexample2.MainApplication
import com.ishaan.websocketexample2.sockets.CoinbaseService
import com.ishaan.websocketexample2.sockets.FlowStreamAdapter
import com.ishaan.websocketexample2.sockets.models.Subscribe
import com.ishaan.websocketexample2.ui.MainFragment
import com.ishaan.websocketexample2.util.Crypto
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.OkHttpClient
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val client: OkHttpClient,
    private val moshi: Moshi,
    private val application: MainApplication
) : ViewModel() {

    private val _prices = MutableLiveData<Map<String, String?>>()
    val prices: LiveData<Map<String, String?>> = _prices

    private var coinbaseService: CoinbaseService? = null

    private val combinedPrices = mutableMapOf<String, String?>().also { prices ->
        Crypto.values().forEach { crytpo ->
            prices[crytpo.id] = null
        }
    }

    fun createService(fragment: MainFragment) {
        val scarlet = Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory("wss://ws-feed.pro.coinbase.com"))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(FlowStreamAdapter.Factory())
            .lifecycle(AndroidLifecycle.ofLifecycleOwnerForeground(application, fragment))
            .build()

        if (coinbaseService == null) {
            coinbaseService = scarlet.create()
            observeWebSocket()
            observeTicker()
        }
    }

    private fun observeWebSocket() {
        coinbaseService?.observeWebSocket()
            ?.flowOn(Dispatchers.IO)
            ?.onEach { event ->
                if (event !is WebSocket.Event.OnMessageReceived) {
                    Log.d("PUI", "Event = ${event::class.java.simpleName}")
                }

                if (event is WebSocket.Event.OnConnectionOpened<*>) {
                    coinbaseService?.sendSubscribe(
                        Subscribe(
                            productIds = Crypto.values().map { it.id },
                            channels = listOf("ticker")
                        )
                    )
                }
            }?.launchIn(viewModelScope)
    }

    private fun observeTicker() {
        coinbaseService?.observeTicker()
            ?.flowOn(Dispatchers.IO)
            ?.onEach {
                combinedPrices[it.productId] = it.price

                _prices.postValue(combinedPrices)
            }
            ?.launchIn(viewModelScope)
    }
}