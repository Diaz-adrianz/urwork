package com.urwork.mobile.api

import com.urwork.mobile.models.TaskModelList
import com.urwork.mobile.models.UserModel
import retrofit2.Call
import retrofit2.http.GET

interface TaskApi {
    @GET("/tasks/my")
    fun myTasks(): Call<TaskModelList>
}