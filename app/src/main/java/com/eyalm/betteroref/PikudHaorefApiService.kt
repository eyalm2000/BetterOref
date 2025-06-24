package com.eyalm.betteroref

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://www.oref.org.il/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface PikudHaorefApiService {

    @GET("warningMessages/alert/Alerts.json")
    suspend fun getAlertsAsRawString(): Response<String?>

}

object PikudHaorefApi {
    val retrofitService: PikudHaorefApiService by lazy {
        retrofit.create(PikudHaorefApiService::class.java)
    }
}