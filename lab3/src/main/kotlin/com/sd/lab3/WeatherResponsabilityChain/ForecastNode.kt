package com.sd.lab3.WeatherResponsabilityChain

import com.sd.lab3.Chain.Chainable
import com.sd.lab3.interfaces.WeatherForecastInterface
import com.sd.lab3.pojo.WeatherForecastData
import org.springframework.stereotype.Service

@Service
class ForecastNode( private val service: WeatherForecastInterface): Chainable<Pair<Double, Double> , WeatherForecastData> {
    override fun proceed(input: Pair<Double, Double>): WeatherForecastData {
        return service.getForecastData(input)
    }

}