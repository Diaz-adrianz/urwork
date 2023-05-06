package com.urwork.mobile.api

import com.urwork.mobile.models.*
import retrofit2.Call
import retrofit2.http.*

interface AuthApi {
    //    @POST("/gauth")
//    fun gauth(@Body model: UserModelData ):Call<UserModel>
//
    @Headers("Content-Type: application/json")
    @POST("/auth/signup")
    fun signup(@Body model: UserModelData): Call<UserModel>

    @Headers("Content-Type: application/json")
    @POST("/auth/signin")
    fun signin(@Body model: UserModelData): Call<UserModel>

    @GET("/users/my")
    fun userinfo(): Call<UserModel>

    @Headers("Content-Type: application/json")
    @PUT("/users/my")
    fun updateAccount(@Body model: UpdateUserModelData): Call<UserModel>

    @DELETE("/auth/signout")
    fun signout(): Call<UserModel>
}