package com.sd.lab3.Chain

import com.sd.lab3.interfaces.TimeInterface
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

class ChainRunner(startClass: KClass<out Chainable<*, *>>) {
    val handlers=mutableListOf<Chainable<*,*>>()

    init {
        var currentClass: KClass<out Chainable<*, *>>? = startClass

        while (currentClass!=null)
        {
            val handler = currentClass.createInstance() as Chainable<*, *>
            handlers.add(handler)

            val annotation = currentClass.findAnnotation<ChainNext>()
            currentClass= annotation?.nextClass
        }
    }

    fun execute(initialInput: Any): Any? {
        var currentData: Any? = initialInput

        for (handler in handlers) {
            @Suppress("UNCHECKED_CAST")
            val castedHandler = handler as Chainable<Any?, Any?>
            currentData = castedHandler.proceed(currentData)
        }

        return currentData
    }
}