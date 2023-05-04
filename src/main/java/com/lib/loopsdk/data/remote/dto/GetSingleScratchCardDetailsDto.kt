package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class GetSingleScratchCardDetailsDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("backgroundImage")
        val backgroundImage: BackgroundImage,
        @SerializedName("benefits")
        val benefits: List<Benefit>,
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("isUnlockable")
        val isUnlockable: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("pointsToUnlock")
        val pointsToUnlock: Int,
        @SerializedName("remaingAttempts")
        val remaingAttempts: Int,
        @SerializedName("remaningPoints")
        val remaningPoints: Int,
        @SerializedName("termsAndConditions")
        val termsAndConditions: String,
        @SerializedName("totalPoints")
        val totalPoints: String
    ) {
        data class BackgroundImage(
            @SerializedName("id")
            val id: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("url")
            val url: String,
        )

        data class Benefit(
            @SerializedName("benefitDetails")
            val benefitDetails: BenefitDetails,
            @SerializedName("benefitId")
            val benefitId: String,
            @SerializedName("benefitType")
            val benefitType: Int,
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