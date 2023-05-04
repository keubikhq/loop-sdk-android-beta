package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class SingleCouponDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("couponClassification")
        val couponClassification: Int,
        @SerializedName("couponCode")
        val couponCode: String,
        @SerializedName("couponCodeType")
        val couponCodeType: Int,
        @SerializedName("couponStatus")
        val couponStatus: Int,
        @SerializedName("description")
        val description: String,
        @SerializedName("discountPercent")
        val discountPercent: Int,
        @SerializedName("displayName")
        val displayName: String,
        @SerializedName("expiredOn")
        val expiredOn: Any,
        @SerializedName("giftedOn")
        val giftedOn: Any,
        @SerializedName("isExpired")
        val isExpired: Any,
        @SerializedName("label")
        val label: String,
        @SerializedName("maxDiscountValue")
        val maxDiscountValue: Int,
        @SerializedName("threshold")
        val threshold: Int,
        @SerializedName("transactionId")
        val transactionId: String,
        @SerializedName("unlockedOn")
        val unlockedOn: String,
        @SerializedName("usedOn")
        val usedOn: String
    )

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}