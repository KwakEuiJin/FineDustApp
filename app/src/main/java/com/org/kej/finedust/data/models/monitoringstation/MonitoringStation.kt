package com.org.kej.finedust.data.models.monitoringstation


import com.google.gson.annotations.SerializedName
import com.org.kej.finedust.domain.model.MonitoringStationModel

data class MonitoringStation(
    @SerializedName("addr")
    val addr: String?,
    @SerializedName("stationName")
    val stationName: String?,
    @SerializedName("tm")
    val tm: Double?
)

fun MonitoringStation.toMonitoringStationModel() = MonitoringStationModel(
    addr = addr ?: "",
    stationName = stationName ?: "",
    tm = tm ?: 0.0
)