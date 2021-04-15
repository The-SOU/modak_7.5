package com.example.modaktestone.navigation.util


import com.example.modaktestone.kakao.Module
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.ResponseBody
import io.reactivex.Single
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object ApiClient {
    const val TOUR_BASE_URL = "http://api.visitkorea.or.kr/openapi/service/"
    const val FCM_URL = "https://fcm.googleapis.com/"
}

interface FcmInterface {
    @POST("fcm/send")
    fun sendNotification(
        @Body notification: NotificationBody
    ): Single<ResponseBody>
}

data class NotificationBody(val to : String?, val data: NotificationData){
    data class NotificationData(val title: String?, val body: String?)
}

