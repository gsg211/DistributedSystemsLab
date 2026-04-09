package com.lab7.logger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LoggerApplication

fun main(args: Array<String>) {
	runApplication<LoggerApplication>(*args)
}
