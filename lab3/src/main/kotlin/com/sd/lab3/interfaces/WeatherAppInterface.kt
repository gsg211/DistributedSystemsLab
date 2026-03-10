package com.sd.lab3.interfaces

import com.sd.lab3.pojo.WeatherForecastData

interface WeatherAppInterface {
    fun forecast(city: String): WeatherForecastData
}