package com.urwork.mobile.api

import com.urwork.mobile.models.AuthModel
import com.urwork.mobile.models.UserModel
import com.urwork.mobile.models.UserModelData
import com.urwork.mobile.models.UserModelList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
//    @POST("/gauth")
//    fun gauth(@Body model: UserModelData ):Call<UserModel>
//
//    @POST("/signup")
//    fun signup(@Body model: UserModelData ):Call<UserModel>

    @Headers("Content-Type: application/json")
    @POST("/auth/signin")
    fun signin(@Body model: UserModelData ):Call<UserModel>

    @GET("/users/my")
    fun userinfo(): Call<UserModel>

    @DELETE("/auth/signout")
    fun signout(): Call<UserModel>
}