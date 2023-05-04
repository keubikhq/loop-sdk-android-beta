package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class RedeemSpinWheelDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("amount")
        val amount: Int,
        @SerializedName("benefitDetails")
        val benefitDetails: BenefitDetails,
        @SerializedName("benefitId")
        val benefitId: String,
        @SerializedName("benefitType")
        val benefitType: Int,
        @SerializedName("messageText")
        val messageText: String,
        @SerializedName("typeId")
        val typeId: String
    ) {
        data class BenefitDetails(
            @SerializedName("couponClassification")
            val couponClassification: Int,
            @SerializedName("discountPercent")
            val discountPercent: Int,
            @SerializedName("displayName")
            val displayName: String,
            @SerializedName("featuredImage")
            val featuredImage: String,
            @SerializedName("isFeature")
            val isFeature: Int,
            @SerializedName("maxDiscountValue")
            val maxDiscountValue: Int,
            @SerializedName("threshold")
            val threshold: Int
        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}