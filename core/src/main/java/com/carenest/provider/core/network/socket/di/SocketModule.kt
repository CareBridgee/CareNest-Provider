package com.carenest.provider.core.network.socket.di

import com.carenest.provider.core.network.socket.client.NurseSocketClient
import com.carenest.provider.core.network.socket.client.NurseSocketClientImpl
import com.carenest.provider.core.network.socket.stomp.StompClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SocketModule {

    @Binds
    @Singleton
    abstract fun bindNurseSocketClient(
        nurseSocketClientImpl: NurseSocketClientImpl
    ): NurseSocketClient

    companion object {
        @Provides
        @Singleton
        fun provideStompClient(
            httpClient: HttpClient
        ): StompClient = StompClient(httpClient)
    }
}
