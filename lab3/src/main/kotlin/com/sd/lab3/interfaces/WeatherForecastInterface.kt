package com.sd.lab3.interfaces

import com.sd.lab3.pojo.WeatherForecastData

interface WeatherForecastInterface {
    fun getForecastData(locationId: Pair<Double, Double>): WeatherForecastData
}