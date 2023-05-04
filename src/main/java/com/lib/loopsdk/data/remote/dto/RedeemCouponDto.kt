package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class RedeemCouponDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("couponCode")
        val couponCode: String,
        @SerializedName("couponCodeType")
        val couponCodeType: Int,
        @SerializedName("description")
        val description: String,
        @SerializedName("displayName")
        val displayName: String
    )

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}