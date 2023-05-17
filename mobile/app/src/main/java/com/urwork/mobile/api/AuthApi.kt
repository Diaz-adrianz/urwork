package com.urwork.mobile.api

import com.urwork.mobile.models.*
import okhttp3.MultipartBody
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

    @GET("/users/{type}")
    fun userinfo(@Path("type") type: String = "my"): Call<UserModel>

    @GET("/users")
    fun getUsers(
        @Query("search") search: String = "",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Call<UserModelList>

    @Headers("Content-Type: application/json")
    @PUT("/users/my")
    fun updateAccount(@Body model: UpdateUserModelData): Call<UserModel>

    @DELETE("/auth/signout")
    fun signout(): Call<UserModel>

    @PUT("/users/my/photo")
    @Multipart
    fun setPhoto(@Part photo: MultipartBody.Part): Call<UserModel>

}