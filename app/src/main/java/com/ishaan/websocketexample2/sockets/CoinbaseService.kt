package com.ishaan.websocketexample2.sockets

import com.ishaan.websocketexample2.sockets.models.Subscribe
import com.ishaan.websocketexample2.sockets.models.Ticker
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

interface CoinbaseService {

    @Receive
    fun observeWebSocket(): Flow<WebSocket.Event>

    @Send
    fun sendSubscribe(subscribe: Subscribe)

    @Receive
    fun observeTicker(): Flow<Ticker>
}