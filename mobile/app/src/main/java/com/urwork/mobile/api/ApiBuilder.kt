package com.urwork.mobile.api

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.urwork.mobile.services.TinyDB
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiBuilder {
    private var token: String = ""

    private val client = OkHttpClient.Builder()
        .addInterceptor{chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()

            if (token != "") {
                builder.addHeader("token", token)
            }

            val request = builder.build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://urwork.vercel.app")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>, _token: String?): T {
        if (_token != null) {
            token = _token
        }
        return retrofit.create(service)
    }

    fun setToken(_token: String): ApiBuilder{
        token = _token
        return this
    }
}