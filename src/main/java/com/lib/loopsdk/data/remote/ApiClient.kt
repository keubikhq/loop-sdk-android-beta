package com.lib.loopsdk.data.remote

import com.lib.loopsdk.BuildConfig
import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponseAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    fun initRetrofit(): ApiService {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(makeOkHttpClient())
            .build()
            .create(ApiService::class.java)
    }

    private fun makeOkHttpClient(): OkHttpClient{

        return OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .apply {
                networkInterceptors().add(Interceptor { chain->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .method(original.method, original.body)
                        .build()
                    return@Interceptor chain.proceed(request)
                })
                if (BuildConfig.DEBUG) addInterceptor(makeLoggingInterceptor())
                addInterceptor(AuthInterceptor())
            }.build()
    }

    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {

        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    }

}