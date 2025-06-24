package com.eyalm.betteroref
import com.google.gson.annotations.SerializedName

data class AlertsResponse(

    @SerializedName("id")
    val id: String,

    @SerializedName("cat")
    val category: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("data")
    val cities: List<String>,

    @SerializedName("desc")
    val description: String
)