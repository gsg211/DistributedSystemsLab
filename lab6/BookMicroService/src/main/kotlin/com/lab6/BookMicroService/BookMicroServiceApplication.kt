package com.lab6.BookMicroService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookMicroServiceApplication

fun main(args: Array<String>) {
	runApplication<BookMicroServiceApplication>(*args)
}
