package com.urwork.mobile.api

import com.urwork.mobile.models.ProjectModelList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProjectApi {

    @GET("/projects/my")
    fun myProjects(@Query("page") page: Int): Call<ProjectModelList>
}