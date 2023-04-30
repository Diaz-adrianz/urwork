package com.urwork.mobile.models

import com.google.gson.annotations.SerializedName

data class TaskModel(
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: TaskModelData?
)

data class TaskModelList(
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: ArrayList<TaskModelData>?
)

data class TaskModelData (
    @SerializedName("_id") var Id : String? = "",
    @SerializedName("title") var title : String? = "",
    @SerializedName("project_id") var projectId : ProjectModelData?,
    @SerializedName("label") var label : String? = "",
    @SerializedName("completed_date") var completedDate : String? = "",
    @SerializedName("createdAt") var createdAt : String? = "",
    @SerializedName("updatedAt") var updatedAt : String? = "",
    @SerializedName("completed_by") var completedBy : UserModelData?
)

