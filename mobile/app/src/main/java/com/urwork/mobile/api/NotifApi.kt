package com.urwork.mobile.api

import com.urwork.mobile.models.CountModel
import com.urwork.mobile.models.NotifModel
import com.urwork.mobile.models.NotifModelList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NotifApi {
    @GET("/notifs")
    fun myNotifs(): Call<NotifModelList>

    @GET("/notifs/count")
    fun countMyNotifs(): Call<CountModel>

    @GET("/notifs/{key}/read")
    fun readNotif(
        @Path("key") key: String? = "0"
    ): Call<NotifModel>
}