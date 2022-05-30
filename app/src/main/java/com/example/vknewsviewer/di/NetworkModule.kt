package com.example.vknewsviewer.di

import com.example.vknewsviewer.data.Post
import com.example.vknewsviewer.network.BASE_URL
import com.example.vknewsviewer.network.responses.PostResponse
import com.example.vknewsviewer.network.VK_API_VERSION
import com.example.vknewsviewer.network.VkApiService
import com.example.vknewsviewer.network.responses.VkPostDeserializer
import com.example.vknewsviewer.network.responses.VkPostResponseDeserializer
import com.example.vknewsviewer.utils.ACCESS_TOKEN
import com.example.vknewsviewer.utils.LoginSettingsStorage
import com.example.vknewsviewer.utils.OKHTTP_TIMEOUT
import com.example.vknewsviewer.utils.VERSION_PARAMETER
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHTTPClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        tokenInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(tokenInterceptor)
            .addNetworkInterceptor(httpLoggingInterceptor)
            .connectTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(OKHTTP_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        client: OkHttpClient, gsonConverterFactory: GsonConverterFactory,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideVkApiService(retrofit: Retrofit): VkApiService {
        return retrofit.create(VkApiService::class.java)
    }

    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder()
                .registerTypeAdapter(PostResponse::class.java, VkPostResponseDeserializer())
                .registerTypeAdapter(Post::class.java, VkPostDeserializer())
                .create()
        )
    }

    @Provides
    fun provideRxCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    @Provides
    fun provideTokenInterceptor(loginSettingsStorage: LoginSettingsStorage): Interceptor = Interceptor { chain ->
        val savedToken = loginSettingsStorage.getToken()
        val httpUrl = chain.request().url.newBuilder()
            .addQueryParameter(ACCESS_TOKEN, savedToken)
            .addQueryParameter(VERSION_PARAMETER, VK_API_VERSION)
            .build()
        chain.proceed(chain.request().newBuilder().url(httpUrl).build())
    }
}