package com.sd.lab3.WeatherResponsabilityChain

import com.sd.lab3.Chain.ChainRunner
import com.sd.lab3.interfaces.WeatherAppInterface
import com.sd.lab3.pojo.WeatherForecastData
import com.sd.lab3.services.FileBlacklistService
import org.springframework.stereotype.Service

@Service
class WeatherAppWithChain: WeatherAppInterface {
    private lateinit var service: BlacklistNode
    override fun forecast(city: String): WeatherForecastData {
        val runner=ChainRunner(service::class)
        return runner.execute(city) as WeatherForecastData
    }
}