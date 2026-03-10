package com.sd.lab3.controllers


import com.sd.lab3.interfaces.WeatherAppInterface

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class WeatherAppController {
    @Autowired
    @Qualifier("Chain")
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