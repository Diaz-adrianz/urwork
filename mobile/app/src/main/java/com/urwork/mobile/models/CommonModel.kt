package com.urwork.mobile.models

import com.google.gson.annotations.SerializedName

data class CountModel(
    @SerializedName("msg") var msg: String = "",
    @SerializedName("data") var data: Int? = 0
)