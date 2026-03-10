package com.sd.lab3.WeatherResponsabilityChain

import com.sd.lab3.Chain.Chainable
import com.sd.lab3.interfaces.WeatherForecastInterface
import com.sd.lab3.pojo.WeatherForecastData
import com.sd.lab3.services.WeatherForecastService
import org.springframework.stereotype.Service

@Service

class ForecastNode: Chainable<Pair<Double, Double> , WeatherForecastData> {
    private lateinit var service: WeatherForecastInterface
    override fun proceed(input: Pair<Double, Double>): WeatherForecastData {
        return service.getForecastData(input)
    }

}