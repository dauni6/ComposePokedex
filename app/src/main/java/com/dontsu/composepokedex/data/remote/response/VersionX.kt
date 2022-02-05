package com.dontsu.composepokedex.data.remote.response


import com.google.gson.annotations.SerializedName

data class VersionX(
    @SerializedName("name")
    val name: String?,
    @SerializedName("url")
    val url: String?
)