package com.sd.lab3.services

import com.sd.lab3.Chain.ChainRunner
import com.sd.lab3.WeatherResponsabilityChain.BlacklistNode
import com.sd.lab3.interfaces.WeatherAppInterface
import com.sd.lab3.pojo.WeatherForecastData
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service("Chain")
class WeatherChain(private val context: ApplicationContext): WeatherAppInterface {
    override fun forecast(city: String): WeatherForecastData {
        println("CHAINED")

        val runner= ChainRunner(BlacklistNode::class, context)
        return runner.execute(city) as WeatherForecastData
    }
}