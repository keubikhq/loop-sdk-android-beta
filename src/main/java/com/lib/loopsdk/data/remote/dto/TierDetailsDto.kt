package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class TierDetailsDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("tierData")
        val tierData: List<TierData>,
        @SerializedName("userDetails")
        val userDetails: UserDetails
    ) {
        data class TierData(
            @SerializedName("badgeImage")
            val badgeImage: String,
            @SerializedName("cutOffValue")
            val cutOffValue: String,
            @SerializedName("cutOnValue")
            val cutOnValue: String,
            @SerializedName("isLast")
            val isLast: Boolean,
            @SerializedName("nextTierId")
            val nextTierId: String,
            @SerializedName("remaingPoints")
            val remaingPoints: Int,
            @SerializedName("tierCoupons")
            val tierCoupons: Int,
            @SerializedName("tierID")
            val tierID: String,
            @SerializedName("tierName")
            val tierName: String,
            @SerializedName("tierOrder")
            val tierOrder: Int,
            @SerializedName("tierScratchCards")
            val tierScratchCards: Int,
            @SerializedName("tierSpinWheel")
            val tierSpinWheel: Int,
            @SerializedName("tierUnlockDate")
            var tierUnlockDate: String = "",
            var isExpanded: Boolean = false,
            var isCurrentTier: Boolean = false
        )

        data class UserDetails(
            @SerializedName("email")
            val email: String,
            @SerializedName("firstName")
            val firstName: String,
            @SerializedName("lastName")
            val lastName: String,
            @SerializedName("phone")
            val phone: Any,
            @SerializedName("pointsBalance")
            val pointsBalance: String,
            @SerializedName("tierDetails")
            val tierDetails: TierDetails,
            @SerializedName("totalPointsBurned")
            val totalPointsBurned: String,
            @SerializedName("totalPointsEarned")
            val totalPointsEarned: String
        ) {
            data class TierDetails(
                @SerializedName("badgeImage")
                val badgeImage: String,
                @SerializedName("tierCoupons")
                val tierCoupons: Int,
                @SerializedName("tierID")
                val tierID: String,
                @SerializedName("tierName")
                val tierName: String,
                @SerializedName("tierScratchCards")
                val tierScratchCards: Int,
                @SerializedName("tierSpinWheel")
                val tierSpinWheel: Int,
                @SerializedName("tierUnlockDate")
                val tierUnlockDate: String
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