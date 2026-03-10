package com.sd.lab3.WeatherResponsabilityChain

import com.sd.lab3.Chain.ChainNext
import com.sd.lab3.Chain.Chainable
import com.sd.lab3.interfaces.LocationSearchInterface
import com.sd.lab3.services.LocationSearchService
import org.springframework.stereotype.Service

@Service
@ChainNext(ForecastNode::class)
class LocationSearchNode: Chainable<String, Pair<Double, Double>> {
    private lateinit var service: LocationSearchInterface
    override fun proceed(input: String): Pair<Double, Double> {
        return service.getLocationId(input)
    }
}