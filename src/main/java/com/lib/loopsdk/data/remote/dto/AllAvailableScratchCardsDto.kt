package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class AllAvailableScratchCardsDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("scratchCards")
        val scratchCards: List<ScratchCard>,
        @SerializedName("totalEntries")
        val totalEntries: Int,
        @SerializedName("totalPages")
        val totalPages: Int,
        @SerializedName("totalPoints")
        val totalPoints: String
    ) {
        data class ScratchCard(
            @SerializedName("benefits")
            val benefits: List<Benefit>,
            @SerializedName("benefitBasicDetails")
            val benefitBasicDetails: BenefitBasicDetails,
            @SerializedName("description")
            val description: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("pointsToUnlock")
            val pointsToUnlock: Int,
            @SerializedName("termsAndConditions")
            val termsAndConditions: String
        ) {
            data class Benefit(
                @SerializedName("amount")
                val amount: Int,
                @SerializedName("benefitType")
                val benefitType: Int,
                @SerializedName("id")
                val id: Int,
                @SerializedName("messageText")
                val messageText: String,
                @SerializedName("typeId")
                val typeId: Int
            )

            data class BenefitBasicDetails(
                @SerializedName("points")
                val points: String,
                @SerializedName("coupons")
                val coupons: Int
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