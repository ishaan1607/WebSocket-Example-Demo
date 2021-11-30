package com.ishaan.websocketexample2.di

import android.content.Context
import com.ishaan.websocketexample2.BuildConfig
import com.ishaan.websocketexample2.MainApplication
import com.ishaan.websocketexample2.sockets.CoinbaseService
import com.ishaan.websocketexample2.sockets.FlowStreamAdapter
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providesApplication(@ApplicationContext context: Context): MainApplication {
        return context as MainApplication
    }

    @Provides
    fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    fun providesHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
            .setLevel(
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                else HttpLoggingInterceptor.Level.NONE
            )
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @Provides
    fun providesScarlet(application: MainApplication, client: OkHttpClient, moshi: Moshi): Scarlet {
        return Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory("wss://ws-feed.pro.coinbase.com"))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(FlowStreamAdapter.Factory())
            .lifecycle(AndroidLifecycle.ofApplicationForeground(application))
            .build()
    }

    @Provides
    fun provideCoinbaseService(scarlet: Scarlet): CoinbaseService {
        return scarlet.create()
    }
}