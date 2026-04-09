package com.lab6.CachingMicroService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CachingMicroServiceApplication

fun main(args: Array<String>) {
	runApplication<CachingMicroServiceApplication>(*args)
}
