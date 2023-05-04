package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class UnlockSpinWheelDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("spinTransactionId")
        val spinTransactionId: String
    )

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}