package com.urwork.mobile.api

import com.urwork.mobile.models.CreateProjectModel
import com.urwork.mobile.models.ProjectModel
import com.urwork.mobile.models.ProjectModelList
import com.urwork.mobile.models.removeImageModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ProjectApi {

    @GET("/projects/my")
    fun myProjects(
        @Query("page") page: Int,
        @Query("search") search: String = "",
        @Query("start") start: String = "",
        @Query("end") end: String = "",
    ): Call<ProjectModelList>

    @GET("/projects")
    fun getProjects(
        @Query("page") page: Int,
        @Query("search") search: String,
        @Query("start") start: String = "",
        @Query("end") end: String = "",
        @Query("author") author: String = ""
    ): Call<ProjectModelList>

    @GET("/projects/{key}")
    fun getProject(
        @Path("key") key: String? = "0"
    ): Call<ProjectModel>

    @GET("/projects/ongoing")
    fun myOngoingProjects(@Query("limit") limit: Int = 10): Call<ProjectModelList>

    @Headers("Content-Type: application/json")
    @POST("/projects")
    fun createProject(@Body model: CreateProjectModel): Call<ProjectModel>

    @Headers("Content-Type: application/json")
    @PUT("/projects/{key}")
    fun updateProject(
        @Path("key") key: String = "",
        @Body model: CreateProjectModel
    ): Call<ProjectModel>

    @DELETE("/projects/{key}")
    fun deleteProject(@Path("key") key: String = ""): Call<ProjectModel>

    @GET("/projects/starit/{key}/{option}")
    fun giveStar(
        @Path("key") key: String? = "0",
        @Path("option") option: String? = "yes"
    ): Call<ProjectModel>

    @POST("/projects/{key}/images")
    @Multipart
    fun addImage(
        @Path("key") key: String? = "0",
        @Part image: MultipartBody.Part
    ): Call<ProjectModel>

    @PUT("/projects/{key}/images")
    fun removeImage(
        @Path("key") key: String? = "0",
        @Body model: removeImageModel
    ): Call<ProjectModel>

}