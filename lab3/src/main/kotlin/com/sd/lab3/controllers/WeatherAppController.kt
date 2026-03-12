package com.sd.lab3.controllers


import com.sd.lab3.interfaces.WeatherAppInterface

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class WeatherAppController {
    @Autowired
    @Qualifier("Orchestrator")
    private lateinit var weatherService: WeatherAppInterface


    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    fun getForecast(@PathVariable location: String, model: Model): String {
        val data= weatherService.forecast(location)
        model.addAttribute("location",location)
        model.addAttribute("forecast",data)
        return "weather"
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(e: Exception,model: Model): String {
        model.addAttribute("error",e.message)
        return "error"
    }

}