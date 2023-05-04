package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class UnlockCouponDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("couponCodeId")
        val couponCodeId: String,
        @SerializedName("transactionId")
        val transactionId: String,
        @SerializedName("isTransferable")
        val isTransferable: String,

    )



    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}