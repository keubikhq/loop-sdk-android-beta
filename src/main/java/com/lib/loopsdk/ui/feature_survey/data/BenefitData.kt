package com.lib.loopsdk.ui.feature_survey.data

data class BenefitData(
    var benefitType: Int?,
    val benefitValue: Any?,
    val messageText: String?,
    val couponClassification: Int?,
    val couponLabel: String?,
    val featuredImage: String?,
    val maxDiscountValue: Int?,
    val pointsToUnlock: Int?,
    val discountPercent: Int?,
    val description: String?,
    val couponName: String?,
    val defaultSuccessMessage: String?
)
