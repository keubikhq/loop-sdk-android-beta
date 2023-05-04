package com.lib.loopsdk.core.util

import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.data.remote.dto.InitializeDto

object Constants {

    const val IDEAL_PAGE_SIZE = 20
    const val NO_INTERNET_MESSAGE = "Please check your network connection and try again."
    val init = Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA")
    const val SURVEY_ID = "survey_id"
    const val BENIFIT_TYPE = "benifit_type"
    const val API_DATE_FORMAT = "dd-MM-yyyy"
    const val API_TIME_FORMAT =  "HH:mm:ss"
    const val LAST_DATE_POP_WAS_SHOWN = "lastDatePopUpWasShown"
    const val COUPON_TYPE_SELECTED = "couponSelected"
    const val COUPON_SORT_TYPE= "couponSortType"
    const val USER_BALANCE= "userBalance"
    const val REDIRECT_TO_UNLOCK = "redirectToUnlock"
    const val RC_NETWORK_ERROR_SCREEN = 1006


    enum class WalletRewardType(val type: Int) {
        CREDIT(1),
        DEBIT(2),
    }

    enum class WalletPointsDebitType(val type: Int, val title: String, val subType: Boolean = false) {
        COUPON_UNLOCK(1, "Coupon Unlocked"),
        SPIN_WHEEL_UNLOCK(2, "Wheel of Fortune Unlocked"),
        GRATIFICATION(3, "Gratification"),
        SCRATCH_CARD_UNLOCKED(4, "Scratch Card Unlocked")
    }

    enum class WalletPointsCreditType(val type: Int, val title: String,val subType: Boolean = false) {
        TIER_ENTRY(1, "Tier Upgrade"),
        SPIN_WHEEL_BENEFIT(2, "Wheel of Fortune Reward"),
        SURVEY_BENEFIT(3, "Quizzlet Reward"),
        GRATIFICATION(4, "Points Gratification"),
        BENEFIT_TO_REFERRER(5, "Referral Reward"),
        BENEFIT_TO_REFEREE(6, "Referral Reward"),
        SCRATCH_CARD_BENEFIT(7, "Scratch Card Reward"),
        QUIZ(8, "Quizzlet Reward"),
        ORDER_BOOKING(9, "Order Booking"),
        BIRTHDAY_TASK_EARNZONE(10, "Birthday task earnzone"),
        ANNIVERSARY_TASK_EARNZONE(11, "Anniversary task earnzone"),
        YOUTUBE_TASK_EARNZONE(12, "Youtube task earnzone"),
        FACEBOOK_TASK_EARNZONE(13, "Facebook task earnzone"),
        LINKEDIN_TASK_EARNZONE(14, "LinkedIn task earnzone"),
        TWITTER_TASK_EARNZONE(15, "Twitter task earnzone"),
        INSTALIKE_TASK_EARNZONE(16, "Instalike task earnzone"),
        INSTAFOLLOW_TASK_EARNZONE(17, "Instafollow task earnzone")

    }

    enum class QuizzTabType(val type: Int) {
        QUIZ(0),
        SURVEY(1),
    }

    enum class CouponType(val type: Int,val title: String) {
        UNLOCKED(0,"Unlocked"),
        REDEEMED(1,"Redeemed"),
        GIFTED(2,"Gifted"),
    }

    enum class COUPONSORTType(val type: Int,val title: String) {
        UNLOCKED(0,"Unlocked"),
        CODE_VIEWED(1,"Code viewed"),
        EXPIRED(2,"Expired"),
        EXPIRING_SOON(3,"Expiring soon")
    }

    enum class CouponUnlockType(val type: Int,val title: String) {
        UNLOCKED(1,"marketplace"),
        REFERRAL(2,"referral"),
        GRATIFICATION(3,"gratification"),
        SCRATCH_CARD(4,"scratch card"),
        WHEEL_OF_FORTUNE(5,"wheel of fortune"),
        SURVEY(6,"survey"),
        GIFT_FROM_FRIEND(7,"received as a gift from your friend"),
        QUIZ(8,"quiz")
    }

    enum class EARN_ZONE_TYPE(val type: Int,val title: String) {
        BIRTHDAY(1,"birthday"),
        ANNIVERSARY(2,"anniversary"),
        YOUTUBE(3,"youtube"),
        FACEBOOK(4,"facebook"),
        LINKEDIN(5,"linkedIn"),
        TWITTER(6,"twitter"),
        INSTALIKE(7,"instaLike"),
        INSTAFOLLOW(8,"instaFollow")
    }


}