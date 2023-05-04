package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class RedeemScratchCardDto(
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
        @SerializedName("description")
        val description: String,
        @SerializedName("messageText")
        val messageText: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("reddemedDate")
        val reddemedDate: String,
        @SerializedName("scratchCardId")
        val scratchCardId: String,
        @SerializedName("termsAndConditions")
        val termsAndConditions: String,
        @SerializedName("transactionId")
        val transactionId: String,
        @SerializedName("typeId")
        val typeId: Int,
        @SerializedName("unlockedDate")
        val unlockedDate: String,
        @SerializedName("value")
        val value: String,
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
            @SerializedName("id")
            val id: String,
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