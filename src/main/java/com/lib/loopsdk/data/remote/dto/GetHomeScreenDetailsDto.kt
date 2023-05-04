package com.lib.loopsdk.data.remote.dto


import com.google.gson.annotations.SerializedName

data class GetHomeScreenDetailsDto(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status
) {
    data class Data(
        @SerializedName("coupons")
        val coupons: List<Coupon>,
        @SerializedName("isFeaturedCouponPresent")
        val isFeaturedCouponPresent: Int,
        @SerializedName("isUnlockedCouponsEnabled")
        val isUnlockedCouponsEnabled: Int,
        @SerializedName("isEarnZoneTaskCompleted")
        val isEarnZoneTaskCompleted: Int,
        @SerializedName("quizlets")
        val quizlets: Quizlets,
        @SerializedName("referralData")
        val referralData: ReferralData,
        @SerializedName("scratchcard")
        val scratchcard: Scratchcard,
        @SerializedName("spinwheel")
        val spinwheel: Spinwheel,
        @SerializedName("tierData")
        val tierData: TierData,
        @SerializedName("totalPoints")
        val totalPoints: String,
        @SerializedName("earnZoneTasks")
        val earnZoneTasks: List<EarnZoneTask>
    ) {
        data class Coupon(
            @SerializedName("categorization")
            val categorization: Int,
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
            @SerializedName("isExpiringSoon")
            val isExpiringSoon: Int,
            @SerializedName("isFeature")
            val isFeature: Int,
            @SerializedName("isUnlockable")
            val isUnlockable: Int,
            @SerializedName("label")
            val label: String,
            @SerializedName("maxDiscountValue")
            val maxDiscountValue: Int,
            @SerializedName("pointsToUnlock")
            val pointsToUnlock: Int,
            @SerializedName("remainingPoints")
            val remainingPoints: Int,
            @SerializedName("targetAudienceSetting")
            val targetAudienceSetting: Int,
            @SerializedName("termsAndConditions")
            val termsAndConditions: String,
            @SerializedName("threshold")
            val threshold: Int
        )

        data class EarnZoneTask(
            @SerializedName("type")
            val type: Int,
            @SerializedName("value")
            val value: Int,
            @SerializedName("url")
            val url:String?
        )

        data class Quizlets(
            @SerializedName("isEnabled")
            val isEnabled: Int,
            @SerializedName("pointsStartsAt")
            val pointsStartsAt: Int,
            @SerializedName("resume")
            val resume: Int,
        )

        data class Scratchcard(
            @SerializedName("isEnabled")
            val isEnabled: Int,
            @SerializedName("isUnlockScratchEnabled")
            val isUnlockScratchEnabled: Int,
            @SerializedName("pointsStartsAt")
            val pointsStartsAt: Int
        )

        data class Spinwheel(
            @SerializedName("isEnabled")
            val isEnabled: Int,
            @SerializedName("isUnlockSpinEnabled")
            val isUnlockSpinEnabled: Int,
            @SerializedName("pointsStartsAt")
            val pointsStartsAt: Int
        )

        data class ReferralData(
            @SerializedName("benefitTypeReferee")
            val benefitTypeReferee: Int,
            @SerializedName("benefitTypeReferrer")
            val benefitTypeReferrer: Int,
            @SerializedName("benefitValueReferee")
            val benefitValueReferee: Int,
            @SerializedName("benefitValueReferrer")
            val benefitValueReferrer: Int,
            @SerializedName("code")
            val code: String,
            @SerializedName("fallbackBenefit")
            val fallbackBenefit: Int,
            @SerializedName("link")
            val link: String,
            @SerializedName("isBlockedReferral")
            val isBlockedReferral: Int
        )

        data class TierData(
            @SerializedName("id")
            val id: String,
            @SerializedName("tierName")
            val tierName: String
        )
    }

    data class Status(
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String
    )
}