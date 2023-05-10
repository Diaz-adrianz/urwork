package com.urwork.mobile.api

import com.urwork.mobile.models.CreateProjectModel
import com.urwork.mobile.models.ProjectModel
import com.urwork.mobile.models.ProjectModelList
import retrofit2.Call
import retrofit2.http.*

interface ProjectApi {

    @GET("/projects/my")
    fun myProjects(@Query("page") page: Int): Call<ProjectModelList>

    @GET("/projects")
    fun getProjects(
        @Query("page") page: Int,
        @Query("search") search: String,
        @Query("start") start: String = "",
        @Query("end") end: String = ""
    ): Call<ProjectModelList>

    @GET("/projects/ongoing")
    fun myOngoingProjects(@Query("limit") limit: Int = 10) : Call<ProjectModelList>

    @Headers("Content-Type: application/json")
    @POST("/projects")
    fun createProject(@Body model: CreateProjectModel): Call<ProjectModel>
}