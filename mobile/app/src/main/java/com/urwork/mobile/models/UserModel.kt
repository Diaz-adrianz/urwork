package com.urwork.mobile.models

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: UserModelData? = null
)

data class AuthModel(
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: String?
)

data class  UserModelList(
    @SerializedName("msg") var msg: String,
    @SerializedName("totalDocs") var totalDocs: Int? = 0,
    @SerializedName("limit") var limit: Int? = 0,
    @SerializedName("totalPages") var totalPages: Int? = 0,
    @SerializedName("page") var page: Int? = 0,
    @SerializedName("prevPage") var prevPage: Int? = null,
    @SerializedName("nextPage") var nextPage: Int? = null,
    @SerializedName("data") var data: ArrayList<UserModelData>?
)

data class UserModelData(
    @SerializedName("_id") var Id: String? = "",
    @SerializedName("first_name") var firstName: String? = "",
    @SerializedName("last_name") var lastName: String? = "",
    @SerializedName("email") var email: String? = "",
    @SerializedName("password") var password: String? = "",
    @SerializedName("about") var about: String? = "",
    @SerializedName("photo") var photo: String? = "",
    @SerializedName("institute") var institute: String? = "",
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
)

data class UpdateUserModelData(
    @SerializedName("first_name") var firstName: String? = "",
    @SerializedName("last_name") var lastName: String? = "",
    @SerializedName("about") var about: String? = "",
    @SerializedName("institute") var institute: String? = "",
)
