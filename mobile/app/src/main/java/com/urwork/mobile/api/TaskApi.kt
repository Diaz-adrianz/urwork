package com.urwork.mobile.api

import com.urwork.mobile.models.TaskModelList
import com.urwork.mobile.models.UserModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TaskApi {
    @GET("/tasks/my")
    fun myTasks(
        @Query("search") search: String = "",
        @Query("page") page: Int = 1
    ): Call<TaskModelList>
}