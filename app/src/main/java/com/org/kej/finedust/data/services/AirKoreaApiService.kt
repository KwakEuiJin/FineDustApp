package com.org.kej.finedust.data.services

import com.org.kej.finedust.BuildConfig
import com.org.kej.finedust.data.models.airquality.AirQualityResponse
import com.org.kej.finedust.data.models.monitoringstation.MonitoringStationsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaApiService {

    @GET("B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" +
            "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
            "&returnType=json")
    suspend fun getNearbyMonitoringStationAir(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ):Response<MonitoringStationsResponse>

    @GET("B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty" +
            "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
            "&returnType=json" +
            "&dataTerm=DAILY" +
            "&ver=1.3")
    suspend fun getRealtimeAirQualities(
        @Query("stationName") stationName: String,
    ):Response<AirQualityResponse>

}