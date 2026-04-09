package com.lab7.auctioner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuctionerApplication

fun main(args: Array<String>) {
	runApplication<AuctionerApplication>(*args)
}
