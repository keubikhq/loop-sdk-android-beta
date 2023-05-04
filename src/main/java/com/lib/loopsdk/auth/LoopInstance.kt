package com.lib.loopsdk.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.view.animation.AnimationUtils
import com.google.gson.reflect.TypeToken
import com.lib.loopsdk.BuildConfig
import com.lib.loopsdk.R
import com.lib.loopsdk.core.util.Constants.init
import com.lib.loopsdk.core.util.Prefs
import com.lib.loopsdk.core.util.RxBusInitializeRelay
import com.lib.loopsdk.core.util.RxBusOnboardingRelay
import com.lib.loopsdk.core.util.UIState
import com.lib.loopsdk.data.remote.ApiClient
import com.lib.loopsdk.data.remote.ApiService
import com.lib.loopsdk.data.remote.customnetworkadapter.NetworkResponse
import com.lib.loopsdk.data.remote.customnetworkadapter.executeWithRetry
import com.lib.loopsdk.data.remote.dto.InitializeDto
import com.lib.loopsdk.exception.LoopSDKException
import com.lib.loopsdk.ui.feature_landing_home.presentation.LandingHomeActivity
import com.lib.loopsdk.ui.feature_loyalty_intro.presentation.IntroductionActivity
import com.lib.loopsdk.ui.feature_loyalty_intro.presentation.SignUpIntroActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Field
import timber.log.Timber
import java.util.*

object LoopInstance {

    private val apiService = ApiClient.initRetrofit()

    fun initialize (
        context: Context
    ){
        Prefs.Builder()
            .setContext(context)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName("fct-loop-sdk")
            .setUseDefaultSharedPreference(true)
            .build()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .apply {
                try {
                    val loopInstanceId: String? = metaData.getString("com.lib.loopsdk.INSTANCE_ID")
                    val loopAccessKey: String? = metaData.getString("com.lib.loopsdk.LOOP_ACCESS_KEY")

                    if(loopInstanceId.isNullOrEmpty()){
                        throw LoopSDKException("INSTANCE_ID not provided")
                    }else if(loopAccessKey.isNullOrEmpty()) {
                        throw LoopSDKException("LOOP_ACCESS_KEY not provided")
                    }
                    Prefs.putString("INSTANCE_ID", loopInstanceId)
                    Prefs.putString("LOOP_ACCESS_KEY", loopAccessKey)
                    if(Prefs.getString("DEVICE_ID").isNullOrEmpty()){
                        Prefs.putString("DEVICE_ID", UUID.randomUUID().toString())
                    }
                    GlobalScope.launch {
                        val response = executeWithRetry(times = 10000) {
                            apiService.initialize(
                                loopInstanceId,
                                loopAccessKey
                            )
                        }
                        when (response) {
                            is NetworkResponse.Success -> {
                                if (response.body.status.code != 200) {
                                    Timber.d("error")
                                    withContext(Dispatchers.Main){
                                        RxBusInitializeRelay.accept(response.body?.status?.message?:"")
                                    }
                                } else {
                                    Prefs.putNonPrimitiveData("INIT_DATA", response.body.data)
                                    Prefs.putNonPrimitiveData("BRAND_THEME", response.body.data.brandTheme)
                                    Prefs.putInt("IS_SIGNUP_CONSENT_REQUIRED", response.body.data.isSignupConsentRequired)
                                    withContext(Dispatchers.Main){
                                        RxBusInitializeRelay.accept("success")
                                    }
                                }
                            }

                            is NetworkResponse.ServerError -> {
                                withContext(Dispatchers.Main){
                                    RxBusInitializeRelay.accept(response.body?.status?.message?:"")
                                }
                            }

                            is NetworkResponse.NetworkError -> {

                            }

                            is NetworkResponse.UnknownError -> {
                                Timber.d("error")
                            }

                        }
                    }
                } catch (e: LoopSDKException){
                    e.printStackTrace()
                }

            }
    }

    fun loginCustomer(
        context: Context,
        mobile: String? = null,
        email: String? = null,
        userIdentity: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        countryCode: String = "+91",
    ){
        if (init.primaryUserId == 1 && email.isNullOrEmpty()) {
            throw LoopSDKException("email must be provided")
        }else if (init.primaryUserId == 2 && mobile.isNullOrEmpty()) {
            throw LoopSDKException("mobile number must be provided")
        }else {
            GlobalScope.launch {
                val response = executeWithRetry(times = 10000) {
                    apiService.login(
                        mobile,
                        email,
                        userIdentity,
                        Prefs.getString("DEVICE_ID")!!,
                        countryCode
                    )
                }
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.status.code != 200) {
                            Timber.d("error")
                        } else {
                            Prefs.putString("AUTH_TOKEN", response.body.data.authToken)
                            Prefs.putInt("IS_NEW_USER", response.body.data.isNewCustomer)
                            Prefs.putInt("HAS_SIGNED_UP", response.body.data.hasSignedUp)
                            val isSignupConsentRequired = Prefs.getInt("IS_SIGNUP_CONSENT_REQUIRED")
                            if (response.body.data.hasSignedUp == 0 && isSignupConsentRequired == 1){
                                Intent(context, IntroductionActivity::class.java).apply {
                                    context.startActivity(this)
                                }
                            }else {
                                Intent(context, LandingHomeActivity::class.java).apply {
                                    context.startActivity(this)
                                }
                            }
                            if(context is Activity) context.finish()
                        }
                    }

                    is NetworkResponse.ServerError -> {
                        Timber.d("error")
                    }

                    is NetworkResponse.NetworkError -> {

                    }

                    is NetworkResponse.UnknownError -> {
                        Timber.d("error")
                    }

                }
            }
        }

    }

    fun launch(context: Context){
        if(Prefs.getNonPrimitiveData<InitializeDto.Data>(object: TypeToken<InitializeDto.Data>(){}.type, "INIT_DATA") == null){
            return
        }

        val authToken = Prefs.getString("AUTH_TOKEN")
        val hasSignedUp = Prefs.getInt("HAS_SIGNED_UP", 0)
        if(authToken.isNullOrEmpty()){
            Handler().postDelayed({
                Intent(context, SignUpIntroActivity::class.java).apply {
                    context.startActivity(this)
                }
            }, 100)
        }else if (hasSignedUp == 0){
            Handler().postDelayed({
                Intent(context, IntroductionActivity::class.java).apply {
                    context.startActivity(this)
                }
            },100)
        }else {
            Handler().postDelayed({
                Intent(context, LandingHomeActivity::class.java).apply {
                    context.startActivity(this)
                }
            }, 100)
        }
    }

    fun updateCustomerProfile(
        mobileNumber: String? = null,
        email: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        countryCode: String? = null,
        dateOfBirth: String? = null,
        gender: String? = null,
        address: String? = null,
        anniversary: String? = null,
        primaryAddress: String? = null,
        stateName: String? = null,
        cityName: String? = null,
        pinCode: String? = null,
        secondaryAddress: String? = null,
    ){
        val hashMap = HashMap<String, String?>()
        hashMap["firstName"] = firstName
        hashMap["lastName"] = lastName
        hashMap["email"] = email
        hashMap["countryCode"] = countryCode
        hashMap["mobileNumber"] = mobileNumber
        hashMap["dob"] = dateOfBirth
        hashMap["gender"] = gender
        hashMap["address"] = address
        hashMap["anniversary"] = anniversary
        hashMap["primaryAddress"] = primaryAddress
        hashMap["stateName"] = stateName
        hashMap["cityName"] = cityName
        hashMap["pinCode"] = pinCode
        hashMap["secondaryAddress"] = secondaryAddress

        GlobalScope.launch {
            val response = apiService.updateProfile(hashMap)

            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        Timber.d("error")
                    } else {

                    }
                }

                is NetworkResponse.ServerError -> {
                    Timber.d("error")
                }

                is NetworkResponse.NetworkError -> {

                }

                is NetworkResponse.UnknownError -> {
                    Timber.d("error")
                }

            }
        }

    }

    fun deleteCustomerProfile(){
        GlobalScope.launch {
            val response = apiService.deleteProfile()

            when (response) {
                is NetworkResponse.Success -> {
                    if (response.body.status.code != 200) {
                        Timber.d("error")
                    } else {

                    }
                }

                is NetworkResponse.ServerError -> {
                    Timber.d("error")
                }

                is NetworkResponse.NetworkError -> {

                }

                is NetworkResponse.UnknownError -> {
                    Timber.d("error")
                }

            }
        }

    }



}