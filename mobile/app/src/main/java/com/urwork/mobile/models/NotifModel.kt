package com.urwork.mobile.models

import com.google.gson.annotations.SerializedName

data class NotifModel(
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: NotifModelData?
)

data class NotifModelList(
    @SerializedName("msg") var msg: String,
    @SerializedName("totalDocs") var totalDocs: Int? = 0,
    @SerializedName("limit") var limit: Int? = 0,
    @SerializedName("totalPages") var totalPages: Int? = 0,
    @SerializedName("page") var page: Int? = 0,
    @SerializedName("prevPage") var prevPage: Int? = null,
    @SerializedName("nextPage") var nextPage: Int? = null,
    @SerializedName("data") var data: ArrayList<NotifModelData>?
)

data class NotifModelData(
    @SerializedName("_id") var Id: String? = "",
    @SerializedName("user_id") var userId: String? = "",
    @SerializedName("title") var title: String? = "",
    @SerializedName("message") var message: String? = "",
    @SerializedName("is_read") var isRead: Boolean? = false,
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
)
