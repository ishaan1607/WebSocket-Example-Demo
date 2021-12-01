package com.ishaan.websocketexample2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.websocketexample2.sockets.CoinbaseService
import com.ishaan.websocketexample2.sockets.models.Subscribe
import com.ishaan.websocketexample2.util.Crypto
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val coinbaseService: CoinbaseService
) : ViewModel() {

    private val _prices = MutableLiveData<Map<String, String?>>()
    val prices: LiveData<Map<String, String?>> = _prices

    private val combinedPrices = mutableMapOf<String, String?>().also { prices ->
        Crypto.values().forEach { crytpo ->
            prices[crytpo.id] = null
        }
    }

    init {
        observeWebSocket()
        observeTicker()
    }

    private fun observeWebSocket() {
        coinbaseService.observeWebSocket()
            .flowOn(Dispatchers.IO)
            .onEach { event ->
                if (event !is WebSocket.Event.OnMessageReceived) {
                    Log.d("PUI", "Event = ${event::class.java.simpleName}")
                }

                if (event is WebSocket.Event.OnConnectionOpened<*>) {
                    coinbaseService.sendSubscribe(
                        Subscribe(
                            productIds = Crypto.values().map { it.id },
                            channels = listOf("ticker")
                        )
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun observeTicker() {
        coinbaseService.observeTicker()
            .flowOn(Dispatchers.IO)
            .onEach {
                combinedPrices[it.productId] = it.price

                _prices.postValue(combinedPrices)
            }
            .launchIn(viewModelScope)
    }
}