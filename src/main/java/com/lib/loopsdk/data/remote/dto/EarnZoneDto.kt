package com.lib.loopsdk.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EarnZoneDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
){
    data class Data(
        @SerializedName("available")
        val available: List<Available>,
        @SerializedName("completed")
        val completed: List<Completed>,
        @SerializedName("totalPoints")
        val totalPoints:Float
    )
    {
        data class Available(
            @SerializedName("type")
            val type: Int,
            @SerializedName("value")
            val value: Int,
            @SerializedName("url")
            val url:String?
        )

        data class Completed(
            @SerializedName("type")
            val type: Int,
            @SerializedName("value")
            val value: Int,
            @SerializedName("url")
            val url:String?
        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}
