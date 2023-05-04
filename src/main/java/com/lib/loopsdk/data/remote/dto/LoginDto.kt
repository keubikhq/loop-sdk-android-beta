package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("authToken")
        val authToken: String,
        @SerializedName("build")
        val build: String,
        @SerializedName("countryCode")
        val countryCode: String,
        @SerializedName("device")
        val device: String,
        @SerializedName("hasSignedUp")
        val hasSignedUp: Int,
        @SerializedName("id")
        val id: String,
        @SerializedName("instanceEnv")
        val instanceEnv: String,
        @SerializedName("instanceId")
        val instanceId: String,
        @SerializedName("isActive")
        val isActive: Int,
        @SerializedName("isNewCustomer")
        val isNewCustomer: Int,
        @SerializedName("loopInstanceId")
        val loopInstanceId: String,
        @SerializedName("userName")
        val userName: String
    )

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}