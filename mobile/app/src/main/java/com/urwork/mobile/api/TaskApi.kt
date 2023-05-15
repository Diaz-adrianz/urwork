package com.urwork.mobile.api

import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.models.TaskModelList
import com.urwork.mobile.models.UserModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApi {
    @GET("/tasks/my/{status}")
    fun myTasks(
        @Path("status") status: String = "all",
        @Query("search") search: String = "",
        @Query("page") page: Int = 1
    ): Call<TaskModelList>

    @GET("/tasks/{project_id}/{key}/complete")
    fun completeTask(
        @Path("project_id") project_id: String? = "",
        @Path("key") key: String? = ""
    ): Call<TaskModelData>
}