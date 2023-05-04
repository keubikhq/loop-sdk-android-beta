package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class CouponHistoryDto(
    @SerializedName("data")
    val data: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("couponsData")
        val couponsData: List<CouponsData>,
        @SerializedName("totalEntries")
        val totalEntries: Int,
        @SerializedName("totalPages")
        val totalPages: Int
    ) {
        data class CouponsData(
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
            val expiredOn: String,
            @SerializedName("giftedOn")
            val giftedOn: String,
            @SerializedName("isExpired")
            val isExpired: String,
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
            val usedOn: String,
            @SerializedName("termsAndConditions")
            val termsAndConditions : String,
            @SerializedName("isExpiringSoon")
            val isExpiringSoon: Int,
            @SerializedName("isTransferable")
            val isTransferable: Int,
            @SerializedName("unlockOrigin")
            val unlockOrigin: Int
            )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}