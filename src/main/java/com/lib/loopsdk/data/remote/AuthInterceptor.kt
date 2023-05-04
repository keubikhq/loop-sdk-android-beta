package com.lib.loopsdk.data.remote

import com.lib.loopsdk.core.util.Constants
import com.lib.loopsdk.core.util.Prefs
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.pathSegments.containsAll(listOf("auth", "initialize"))){
            return chain.proceed(request)
        } else if (request.url.pathSegments.containsAll(listOf("auth", "login"))) {
            val authenticatedRequest = request.newBuilder()
                .header("loopInstanceId", Prefs.getString("INSTANCE_ID")?:"")
                .header("loopAccessKey", Prefs.getString("LOOP_ACCESS_KEY")?:"")
                .build()
            return chain.proceed(authenticatedRequest)
        } else {
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", Prefs.getString("AUTH_TOKEN")?:"")
                .header("loopInstanceId", Prefs.getString("INSTANCE_ID")?:"")
                .header("loopAccessKey", Prefs.getString("LOOP_ACCESS_KEY")?:"")
                .build()
            return chain.proceed(authenticatedRequest)
        }


    }

}