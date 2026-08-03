package com.carenest.chat.data.di

import com.carenest.chat.data.datasource.ChatDataSource
import com.carenest.chat.data.datasource.ChatDataSourceImp
import com.carenest.chat.data.repository.ChatRepositoryImpl
import com.carenest.chat.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule{

    @Binds
    @Singleton
    abstract fun bindChatDataSource(impl: ChatDataSourceImp): ChatDataSource

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

}