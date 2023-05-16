package com.urwork.mobile.models

import com.google.gson.annotations.SerializedName

data class ProjectModel(
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: ProjectModelData?
)

data class ProjectModelList(
    @SerializedName("msg") var msg: String,
    @SerializedName("totalDocs") var totalDocs: Int? = 0,
    @SerializedName("limit") var limit: Int? = 0,
    @SerializedName("totalPages") var totalPages: Int? = 0,
    @SerializedName("page") var page: Int? = 0,
    @SerializedName("prevPage") var prevPage: Int? = null,
    @SerializedName("nextPage") var nextPage: Int? = null,
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
    @SerializedName("duration_start") var durationStart: String? = "",
    @SerializedName("duration_end") var durationEnd: String? = "",
    @SerializedName("completed_date") var completedDate: String? = "",
    @SerializedName("is_blocked") var isBlocked: Boolean? = false,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("percentage") var percentage: Int? = 0,
    @SerializedName("is_mine") var isMine: Boolean? = false
)

data class CreateProjectModel(
    @SerializedName("title") var title: String? = "",
    @SerializedName("description") var description: String? = "",
    @SerializedName("collaborators") var collaborators: ArrayList<String?>,
    @SerializedName("tags") var tags: ArrayList<String>?,
    @SerializedName("duration_start") var durationStart: String? = "",
    @SerializedName("duration_end") var durationEnd: String? = "",
)
