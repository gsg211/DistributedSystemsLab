package com.sd.lab3.services

import com.sd.lab3.interfaces.BlacklistInterface
import com.sd.lab3.interfaces.LocationSearchInterface
import com.sd.lab3.interfaces.WeatherAppInterface
import com.sd.lab3.interfaces.WeatherForecastInterface
import com.sd.lab3.pojo.WeatherForecastData
import org.springframework.stereotype.Service

@Service("Orchestrator")
class weatherOrchestrator(private val blacklistService: BlacklistInterface,
                          private val locationService: LocationSearchInterface,
                          private val forecastService: WeatherForecastInterface
) : WeatherAppInterface {


    override fun forecast(city: String): WeatherForecastData {
        println("ORCHESTRATED")
        val filteredCity = blacklistService.filter(city)
        if (filteredCity == "undefined") {
            throw Exception("Locatia '$city' este blocata!")
        }

        val coords = locationService.getLocationId(filteredCity)

        return forecastService.getForecastData(coords)
    }
}