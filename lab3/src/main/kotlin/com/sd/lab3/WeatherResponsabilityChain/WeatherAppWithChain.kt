package com.sd.lab3.WeatherResponsabilityChain

import com.sd.lab3.Chain.ChainRunner
import com.sd.lab3.interfaces.WeatherAppInterface
import com.sd.lab3.pojo.WeatherForecastData
import com.sd.lab3.services.FileBlacklistService
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class WeatherAppWithChain(private val context: ApplicationContext): WeatherAppInterface {
    override fun forecast(city: String): WeatherForecastData {
        val runner=ChainRunner(BlacklistNode::class,context)
        return runner.execute(city) as WeatherForecastData
    }
}