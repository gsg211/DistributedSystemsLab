package com.sd.lab3.controllers

import com.sd.lab3.interfaces.BlacklistInterface
import com.sd.lab3.interfaces.WeatherForecastInterface
import com.sd.lab3.interfaces.LocationSearchInterface
import com.sd.lab3.interfaces.WeatherAppInterface
import com.sd.lab3.pojo.WeatherForecastData
import com.sd.lab3.services.FileBlacklistService
import com.sd.lab3.services.TimeService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class WeatherAppController {
    @Autowired
    private lateinit var weatherService: WeatherAppInterface


    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        val data= weatherService.forecast(location)

        return data.toString()
    }

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleAllExceptions(e: Exception): String {
        return "Opa baiatu vezi ca ${e.message}"
    }

}