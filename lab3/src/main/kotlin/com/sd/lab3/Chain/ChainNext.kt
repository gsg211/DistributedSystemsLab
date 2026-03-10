package com.sd.lab3.Chain

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChainNext(val nextClass: KClass<out Chainable<*, *>>)