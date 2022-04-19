package com.uvenal.slot.data.api

import com.uvenal.slot.utils.Constants
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("install")
    suspend fun sendInfoInstall(
        @Field("install_id") install_id: String,
        @Field("carrier_name") carrier_name: String,
        @Field("carrier_id") carrier_id: String,
        @Field("carrier_country") carrier_country: String,
        @Field("carrier_sim_name") carrier_sim_name: String,
        @Field("device_manufacturer") device_manufacturer: String,
        @Field("device_model") device_model: String,
        @Field("device_locale") device_locale: String,
        @Field("os_ver") os_ver: String,
        @Field("time_offset") time_offset: String,
        @Field("time_zone") time_zone: String,
        @Field("package_name") package_name: String,
        @Field("app_ver") app_ver: String
    )

    @FormUrlEncoded
    @POST("fcm")
    suspend fun sendFirebaseToken(
        @Field("install_id") install_id: String,
        @Field("token") token: String
    )

    @FormUrlEncoded
    @POST("deeplink")
    suspend fun sendDeepLink(
        @Field("install_id") install_id: String,
        @Field("campaign") campaign: String
    )

    @FormUrlEncoded
    @POST("log")
    suspend fun sendLog(
        @Field("log_token") log_token: String,
        @Body body: RequestBody
    )

    companion object {
        val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("https://gaplay.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}