package com.org.kej.finedust.presenter.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.org.kej.finedust.DustUtil
import com.org.kej.finedust.data.models.airquality.Grade
import com.org.kej.finedust.data.models.airquality.MeasuredValue
import com.org.kej.finedust.databinding.ActivityMainBinding
import com.org.kej.finedust.presenter.DustState
import com.org.kej.finedust.presenter.DustViewModel
import com.org.kej.finedust.presenter.splash.SplashActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
    }

    private lateinit var stationName: String
    private lateinit var addr: String

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cancellationTokenSource: CancellationTokenSource? = null

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<DustViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initIntentData()
        observeData()
        initRefreshLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
    }

    private fun initRefreshLayout() {
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.refresh.setOnRefreshListener {
            cancellationTokenSource?.let {
                DustUtil.fetchAirQualityData(this, viewModel, it, fusedLocationProviderClient)
            }
        }
    }

    private fun initIntentData() {
        stationName = intent.getStringExtra(SplashActivity.STATION_NAME) ?: ""
        addr = intent.getStringExtra(SplashActivity.ADDR) ?: ""
        viewModel.getMeasuredValue(stationName)
    }

    private fun observeData() {
        try {
            viewModel.dustLiveData.observe(this) {
                when (it) {
                    is DustState.SuccessMonitoringStation -> {
                        stationName = it.monitoringStation.stationName ?:""
                        viewModel.getMeasuredValue(stationName)
                    }

                    is DustState.SuccessMeasureVale -> {
                        displayAurQualityData(it.MeasuredValue)
                        binding.progressBar.visibility = GONE
                        binding.refresh.isRefreshing = false
                    }
                    else -> {
                        binding.errorDescriptionTextView.visibility = VISIBLE
                        binding.contentsLayout.alpha = 0f
                        //TODO ERROR POPUP
                    }
                }
            }
        } catch (e: Exception) {
            //TODO ERROR 팝업
        }

    }


    @SuppressLint("SetTextI18n")
    private fun displayAurQualityData(
        measuredValue: MeasuredValue
    ) {

        binding.contentsLayout.animate().alpha(1f).start()

        binding.measuringStationNameTextView.text = stationName
        binding.measuringStationAddressTextView.text = addr
        (measuredValue.khaiGrade ?: Grade.UNKNOWN).let { grade ->
            binding.root.setBackgroundResource(grade.colorResId)
            binding.totalGradeLabelTextView.text = grade.label
            binding.totalGradeEmojiTextView.text = grade.emoji
        }

        with(measuredValue) {
            binding.fineDustInformationTextView.text = "미세먼지: $pm10Value ㎍/㎥ ${(pm10Grade ?: Grade.UNKNOWN).emoji}"
            binding.ultraFineDustInformationTextView.text = "초미세먼지: $pm25Value ㎍/㎥ ${(pm25Grade ?: Grade.UNKNOWN).emoji}"

            with(binding.so2Item) {
                labelTextView.text = "아황산가스"
                gradleTextView.text = (so2Grade ?: Grade.UNKNOWN).toString()
                valueTextView.text = "$so2Value ppm"
            }
            with(binding.coItem) {
                labelTextView.text = "일산화탄소"
                gradleTextView.text = (coGrade ?: Grade.UNKNOWN).toString()
                valueTextView.text = "$coValue ppm"
            }
            with(binding.o3Item) {
                labelTextView.text = "오존"
                gradleTextView.text = (o3Grade ?: Grade.UNKNOWN).toString()
                valueTextView.text = "$o3Value ppm"
            }
            with(binding.no2Item) {
                labelTextView.text = "이산화질소"
                gradleTextView.text = (no2Grade ?: Grade.UNKNOWN).toString()
                valueTextView.text = "$no2Value ppm"
            }
        }
    }
}