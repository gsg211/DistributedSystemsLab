package com.sd.lab3.WeatherResponsabilityChain

import com.sd.lab3.Chain.ChainNext
import com.sd.lab3.Chain.Chainable
import com.sd.lab3.interfaces.BlacklistInterface
import org.springframework.stereotype.Service

@Service
@ChainNext(LocationSearchNode::class)
class BlacklistNode: Chainable<String, String> {
    private lateinit var service: BlacklistInterface
    override fun proceed(input: String): String {
        return service.filter(input)
    }

}