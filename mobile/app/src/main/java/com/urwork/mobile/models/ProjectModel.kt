package com.urwork.mobile.models

import com.google.gson.annotations.SerializedName

data class ProjectModel(
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: ProjectModelData?
)

data class ProjectModelList(
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: ArrayList<ProjectModelData>?
)

data class ProjectModelData(
    @SerializedName("_id") var Id: String? = "",
    @SerializedName("title") var title: String? = "",
    @SerializedName("description") var description: String? = "",
    @SerializedName("author") var author: UserModelData?,
    @SerializedName("collaborators") var collaborators: List<UserModelData>?,
    @SerializedName("images") var images: List<String>?,
    @SerializedName("tags") var tags: String? = "",
    @SerializedName("stars") var stars: List<String>?,
    @SerializedName("completed_date") var completedDate: String? = "",
    @SerializedName("is_blocked") var isBlocked: Boolean? = false,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
)