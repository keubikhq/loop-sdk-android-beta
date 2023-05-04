package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class UnlockScratchCardDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("benefits")
        val benefits: List<Benefit>,
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("scratchcardTransactionId")
        val scratchcardTransactionId: String,
        @SerializedName("termsAndConditions")
        val termsAndConditions: String
    ) {
        data class Benefit(
            @SerializedName("benefitDetails")
            val benefitDetails: BenefitDetails,
            @SerializedName("benefitId")
            val benefitId: String,
            @SerializedName("benefitType")
            val benefitType: Int,
            @SerializedName("messageText")
            val messageText: String,
            @SerializedName("typeId")
            val typeId: Int,
            @SerializedName("value")
            val value: String
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
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}