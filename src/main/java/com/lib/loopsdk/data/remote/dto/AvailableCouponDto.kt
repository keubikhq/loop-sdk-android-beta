package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class AvailableCouponDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("couponsData")
        val `couponsData`: ArrayList<CouponsData>,
        @SerializedName("totalEntries")
        val totalEntries: Int,
        @SerializedName("totalPages")
        val totalPages: Int
    ) {
        data class CouponsData(
            @SerializedName("couponClassification")
            val couponClassification: Int,
            @SerializedName("description")
            val description: String,
            @SerializedName("discountPercent")
            val discountPercent: Int,
            @SerializedName("displayName")
            val displayName: String,
            @SerializedName("featuredImage")
            val featuredImage: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("isFeature")
            val isFeature: Int,
            @SerializedName("isUnlockable")
            val isUnlockable: Int,
            @SerializedName("maxDiscountValue")
            val maxDiscountValue: Int,
            @SerializedName("pointsToUnlock")
            val pointsToUnlock: Int,
            @SerializedName("remaningPoints")
            val remaningPoints: Int,
            @SerializedName("termsAndConditions")
            val termsAndConditions: String,
            @SerializedName("threshold")
            val threshold: Int,
            @SerializedName("isExpiringSoon")
            val isExpiringSoon: Int,
            @SerializedName("endDate")
            val endDate: String

        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}