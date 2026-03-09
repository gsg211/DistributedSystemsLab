package com.sd.lab3.controllers

import com.sd.lab3.interfaces.WeatherForecastInterface
import com.sd.lab3.interfaces.LocationSearchInterface
import com.sd.lab3.pojo.WeatherForecastData

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class WeatherAppController {
    @Autowired
    private lateinit var locationSearchService: LocationSearchInterface

    @Autowired
    private lateinit var weatherForecastService: WeatherForecastInterface

    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        val locationCoords= locationSearchService.getLocationId(location)
        val rawForecastData: WeatherForecastData = weatherForecastService.getForecastData(locationCoords)

        return rawForecastData.toString()
    }
}