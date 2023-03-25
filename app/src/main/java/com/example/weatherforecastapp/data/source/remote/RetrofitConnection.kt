package com.example.weatherforecastapp.data.source.remote

import com.example.weatherforecastapp.utils.Constants
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

object RetrofitConnection {

    @Volatile
    private var requestInterceptor = Interceptor { chain ->
        val url = chain
            .request()
            .url
            .newBuilder()
            .build()

        val request = chain
            .request()
            .newBuilder()
            .url(url)
            .build()

        return@Interceptor chain.proceed(request)
    }

    // okHttp Interceptor
    @Volatile
    private var client = OkHttpClient
        .Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(150, TimeUnit.SECONDS)
        .readTimeout(50, TimeUnit.SECONDS)
        .callTimeout(50, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .addInterceptor(requestInterceptor)
        .build()


    // retrofit object that take Interceptor client
    //    private var retrofit: Retrofit? = null
    @Volatile
    private var retrofit = Retrofit.Builder()
        .baseUrl(Constants.WHETHER_BASE_URL)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .client(client)
        .build()

    // provide object from class implement Service and Retrofit is this class
    @Synchronized
    fun getServices(): RetrofitServices = retrofit.create(RetrofitServices::class.java)

}
