package com.lib.loopsdk.data.remote

import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import com.lib.loopsdk.data.remote.dto.*
import retrofit2.http.*

@JvmSuppressWildcards
interface ApiService {

    @GET("customer/auth/initialize")
    suspend fun initialize(
        @Header("loopInstanceID") loopInstanceID: String,
        @Header("loopAccessKey") loopAccessKey: String
    ): NetworkResponse<InitializeDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/auth/login")
    suspend fun login(
        @Field("mobile") mobile: String?,
        @Field("email") email: String?,
        @Field("userIdentity") userIdentity: String?,
        @Field("deviceId") deviceId: String,
        @Field("countryCode") countryCode: String = "+91",
        @Field("device") deviceType: Int = 2,
        @Field("build") buildVersionName: String = "1.0.0"
    ): NetworkResponse<LoginDto, ErrorDto>


    @GET("customer/profile/referralBenefits")
    suspend fun getInviteAndEarnData(): NetworkResponse<ProfileReferralBenefitsDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/profile/addReferral")
    suspend fun addReferral(
        @Field("referralCode") referralCode: String,
        @Field("isLast") isLast: Int,
    ): NetworkResponse<ErrorDto, ErrorDto>

    @PUT("customer/join")
    suspend fun joinNow(): NetworkResponse<SignUpDto, ErrorDto>
    @PUT("customer/profile/update")
    suspend fun updateProfile(
        @FieldMap params: Map<String, String?>
    ): NetworkResponse<UpdateCustomerProfileDto, ErrorDto>
    @DELETE("customer/profile/delete")
    suspend fun deleteProfile(): NetworkResponse<DeleteCustomerProfileDto, ErrorDto>

    @GET("customer/coupons")
    suspend fun getAvailableCoupons(
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int = Constants.IDEAL_PAGE_SIZE
    ): NetworkResponse<AvailableCouponDto, ErrorDto>

    @GET("customer/coupons/search")
    suspend fun getSearchCouponList(
        @QueryMap params: Map<String, String>
    ): NetworkResponse<AvailableCouponDto, ErrorDto>

    @GET("customer/coupons/history")
    suspend fun getCouponsHistory(
        @QueryMap params: Map<String, String>,
    ): NetworkResponse<CouponHistoryDto, ErrorDto>

    @GET("customer/coupons/unlocked")
    suspend fun getUnlockedList(
        @QueryMap params: Map<String, String>
    ): NetworkResponse<CouponHistoryDto, ErrorDto>


    @GET("customer/coupons/redeemed")
    suspend fun getRedeemedList(
//        @Query("pageNo") pageNo: Int = 1,
//        @Query("pageSize") pageSize: Int = 20,
        @QueryMap params: Map<String, String>
    ): NetworkResponse<CouponHistoryDto, ErrorDto>

    @GET("customer/coupons/transferred")
    suspend fun getGiftedList(
        @QueryMap params: Map<String, String>
    ): NetworkResponse<CouponHistoryDto, ErrorDto>

    @GET("customer/tierdetails")
    suspend fun getTierDetails(): NetworkResponse<TierDetailsDto, ErrorDto>

    @GET("customer/wallet/points/transactions")
    suspend fun getPointsHistory(
        @Query("pageNo") pageNo: String,
        @Query("sort") sort: String = "desc",
        @Query("pageSize") pageSize: Int = Constants.IDEAL_PAGE_SIZE
    ): NetworkResponse<PointsTransactionDto, ErrorDto>
    @FormUrlEncoded
    @POST("customer/coupons/unlock")
    suspend fun unlockCoupon(
        @Field("couponId") couponId: String
    ): NetworkResponse<UnlockCouponDto, ErrorDto>


    @FormUrlEncoded
    @POST("customer/coupons/redeem")
    suspend fun redeemCoupon(
        @Field("couponTransactionId") couponTransactionId: String
    ): NetworkResponse<RedeemCouponDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/coupons/transfer")
    suspend fun transferCouponViaMobile(
        @Field("couponTransactionId") id: String,
        @Field("mobileNumber") mobileNumber: String,
        @Field("countryCode") countryCode: String
    ): NetworkResponse<TransferCouponDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/coupons/transfer")
    suspend fun transferCouponViaEmail(
        @Field("couponTransactionId") id: String,
        @Field("email") email: String,
    ): NetworkResponse<TransferCouponDto, ErrorDto>

    @GET("constants/getConstants")
    suspend fun getCountryCodes(
        @Query("fields") fields: String = "countryCodes"
    ): NetworkResponse<CountryCodesDto, ErrorDto>
    @GET("customer/surveys")
    suspend fun getAvailableSurvey(
    ): NetworkResponse<SurveyAvailableDto,ErrorDto>

    @GET("customer/surveys/{id}")
    suspend fun getSurveyById(
        @Path("id") id: String
    ): NetworkResponse<SurveyDetailDto, ErrorDto>


    @GET("customer/surveys/{id}/questions")
    suspend fun getQuestions(
        @Path("id") id: String,
    ): NetworkResponse<SurveyQuestionsDto, ErrorDto>

    @POST("customer/surveys/{surveyId}/submit")
    suspend fun submitSurvey(
        @Path("surveyId") name: String,
    ): NetworkResponse<SurveySubmitDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/surveys/{surveyId}/answer")
    suspend fun postAnswerStringTypeSurvey(
        @Path("surveyId") name: String,
        @Field("questionId") questionId: String,
        @Field("answer") answer: String,
        @Field("other") other: String,
    ): NetworkResponse<SurveyAnswerSavedDto,ErrorDto>

    @FormUrlEncoded
    @POST("customer/surveys/{surveyId}/answer")
    suspend fun postAnswerArrayTypeSurvey(
        @Path("surveyId") name: String,
        @Field("questionId") questionId: String,
        @Field("answer") answer: String,
        @Field("other") other: String,
    ): NetworkResponse<SurveyAnswerSavedDto,ErrorDto>

    @FormUrlEncoded
    @POST("customer/surveys/{surveyId}/answer")
    suspend fun postAnswerIntTypeSurvey(
        @Path("surveyId") name: String,
        @Field("questionId") questionId: String,
        @Field("answer") answer: String,
        @Field("other") other: String,
    ): NetworkResponse<SurveyAnswerSavedDto,ErrorDto>

    @GET("customer/home")
    suspend fun getHomeScreenDetails(): NetworkResponse<GetHomeScreenDetailsDto, ErrorDto>

    @GET("customer/wallet/points")
    suspend fun getWalletPoints(): NetworkResponse<GetWalletPointsDto, ErrorDto>

    @GET("customer/quiz")
    suspend fun getAvailableQuiz(
    ): NetworkResponse<QuizAvailableDto,ErrorDto>

    @GET("customer/quiz/{id}")
    suspend fun getQuizById(
        @Path("id") id: String
    ): NetworkResponse<QuizDetailDto, ErrorDto>

    @GET("customer/quiz/{id}/questions")
    suspend fun getQuizQuestions(
        @Path("id") id: String,
    ): NetworkResponse<QuizQuestionsDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/quiz/{quizId}/answer")
    suspend fun postQuizAnswer(
        @Path("quizId") quizId: String,
        @Field("questionId") questionId: String,
        @Field("optionId") optionId: String,
        @Field("isTimeUp") isTimeUp: Int,
    ): NetworkResponse<QuizAnswerSaveDto,ErrorDto>

    @POST("customer/quiz/{quizId}/submit")
    suspend fun submitQuiz(
        @Path("quizId") quizId: String,
    ): NetworkResponse<QuizSubmitDto, ErrorDto>

    @POST("customer/quiz/{quizId}/leave")
    suspend fun leaveQuiz(
        @Path("quizId") quizId: String,
    ): NetworkResponse<QuizSubmitDto, ErrorDto>
    @GET("customer/scratchcards")
    suspend fun getAllAvailableScratchCards(
        @Query("pageNo") pageNo: String,
        @Query("pageSize") pageSize: Int = Constants.IDEAL_PAGE_SIZE
    ): NetworkResponse<AllAvailableScratchCardsDto, ErrorDto>

    @GET("customer/scratchcards/unlocked")
    suspend fun getAllUnlockedScratchCards(
        @Query("pageNo") pageNo: String,
        @Query("pageSize") pageSize: Int = Constants.IDEAL_PAGE_SIZE
    ): NetworkResponse<AllUnlockedScratchCardsDto, ErrorDto>

    @GET("customer/scratchcards/{id}")
    suspend fun getSingleScratchCardDetails(
        @Path("id") id: String
    ): NetworkResponse<GetSingleScratchCardDetailsDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/scratchcards/unlock")
    suspend fun unlockScratchCard(
        @Field("scratchCardId") scratchCardId: String,
    ): NetworkResponse<UnlockScratchCardDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/scratchcards/redeem")
    suspend fun redeemScratchCard(
        @Field("scratchCardTransactionId") scratchCardTransactionId: String,
    ): NetworkResponse<RedeemScratchCardDto, ErrorDto>

    @GET("customer/spinWheel")
    suspend fun getAllAvailableSpinWheel(
        @Query("pageNo") pageNo: String,
        @Query("pageSize") pageSize: Int = Constants.IDEAL_PAGE_SIZE
    ): NetworkResponse<AllAvailableSpinWheelDto, ErrorDto>

    @GET("customer/spinwheel/history")
    suspend fun getAllUnlockedSpinWheel(
        @Query("sort") sort: String = "asc",
        @Query("pageNo") pageNo: String,
        @Query("pageSize") pageSize: Int = Constants.IDEAL_PAGE_SIZE
    ): NetworkResponse<AllUnlockedSpinWheelDto, ErrorDto>

    @GET("customer/spinWheel/{id}")
    suspend fun getSingleSpinWheel(
        @Path("id") id: String
    ): NetworkResponse<SpinWheelDetailDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/spinWheel/unlock")
    suspend fun unlockSpinWheel(
        @Field("spinWheelId") spinWheelId: String,
    ): NetworkResponse<UnlockSpinWheelDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/spinWheel/redeem")
    suspend fun redeemSpinWheel(
        @Field("spinWheelTransactionId") spinWheelTransactionId: String,
    ): NetworkResponse<RedeemSpinWheelDto, ErrorDto>

    @GET("customer/earnzone")
    suspend fun getEarnZone(
    ): NetworkResponse<EarnZoneDto,ErrorDto>


    @FormUrlEncoded
    @POST("customer/earnzone")
    suspend fun earnZoneViaBirthDay(
        @Field("type") type: Int,
        @Field("birthdayDate") birthdayDate:String,
    ): NetworkResponse<EarnZoneRedeemDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/earnzone")
    suspend fun earnZoneViaAnniversary(
        @Field("type") type: Int,
        @Field("anniversaryDate") anniversaryDate:String,
    ): NetworkResponse<EarnZoneRedeemDto, ErrorDto>

    @FormUrlEncoded
    @POST("customer/earnzone")
    suspend fun earnZoneViaSocial(
        @Field("type") type: Int,
    ): NetworkResponse<EarnZoneRedeemDto, ErrorDto>

}