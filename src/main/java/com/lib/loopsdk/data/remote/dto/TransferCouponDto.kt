package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class TransferCouponDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("transferId")
        val transferId: String
    )

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}