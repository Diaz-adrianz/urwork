package com.urwork.mobile.api

import com.urwork.mobile.models.*
import retrofit2.Call
import retrofit2.http.*

interface TaskApi {
    @GET("/tasks/my/{status}")
    fun myTasks(
        @Path("status") status: String = "all",
        @Query("search") search: String = "",
        @Query("page") page: Int = 1
    ): Call<TaskModelList>

    @GET("/tasks/{project_id}")
    fun getTasksInProject(
        @Path("project_id") project_id: String? = "0"
    ): Call<TaskModelList>

    @GET("/tasks/{project_id}/{key}/complete")
    fun completeTask(
        @Path("project_id") project_id: String? = "",
        @Path("key") key: String? = ""
    ): Call<TaskModel>

    @Headers("Content-Type: application/json")
    @POST("/tasks/{project_id}")
    fun createTask(
        @Path("project_id") project_id: String? = "0",
        @Body model: CreateTaskModel
    ): Call<TaskModel>

    @DELETE("/tasks/{project_id}/{key}")
    fun deleteTask(
        @Path("project_id") project_id: String? = "",
        @Path("key") key: String? = ""
    ): Call<TaskModel>

}