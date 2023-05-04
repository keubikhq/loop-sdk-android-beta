package com.lib.loopsdk.ui.feature_scratch_card

import com.google.gson.annotations.SerializedName

data class BenefitGeneric(
    var benefitType: Int = -1,
    var description: String = "",
    var messageText: String = "",
    var name: String = "",
    var value: String = "",
    var couponClassification: Int = -1,
    var discountPercent: Int = -1,
    var displayName: String = "",
    var featuredImage: String = "",
    var id: String = "",
    var isFeature: Int = -1,
    var maxDiscountValue: Int = -1,
    var threshold: Int = -1
)
