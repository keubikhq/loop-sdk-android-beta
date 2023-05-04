package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class SpinWheelDetailDto(
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
        @SerializedName("isUnlockable")
        val isUnlockable: Int,
        @SerializedName("canAttempt")
        val canAttempt: Int,
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
        data class Benefit(
            @SerializedName("amount")
            val amount: Int,
            @SerializedName("benefitDetails")
            val benefitDetails: BenefitDetails,
            @SerializedName("benefitType")
            val benefitType: Int,
            @SerializedName("id")
            val id: String,
            @SerializedName("messageText")
            val messageText: Any,
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