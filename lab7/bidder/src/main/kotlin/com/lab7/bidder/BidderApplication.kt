package com.lab7.bidder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BidderApplication

fun main(args: Array<String>) {
	runApplication<BidderApplication>(*args)
}
