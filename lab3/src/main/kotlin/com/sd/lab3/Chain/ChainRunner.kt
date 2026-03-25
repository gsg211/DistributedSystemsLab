package com.sd.lab3.Chain

import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class ChainRunner(
    startClass: KClass<out Chainable<*, *>>,
    private val context: ApplicationContext
) {
    val handlers = mutableListOf<Chainable<*, *>>()

    init {
        var currentClass: KClass<out Chainable<*, *>>? = startClass

        while (currentClass != null) {
            val handler = context.getBean(currentClass.java) as Chainable<*, *>
            handlers.add(handler)

            val annotation = currentClass.findAnnotation<ChainNext>()
            currentClass = annotation?.nextClass
        }
    }

    fun execute(initialInput: Any): Any? {
        var currentData: Any? = initialInput
        for (handler in handlers) {
            val castedHandler = handler as Chainable<Any?, Any?>
            currentData = castedHandler.proceed(currentData)
        }
        return currentData
    }
}