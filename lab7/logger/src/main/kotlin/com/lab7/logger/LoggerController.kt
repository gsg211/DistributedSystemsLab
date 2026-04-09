package com.lab7.logger

import io.reactivex.rxjava3.core.Single
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoggerController {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/log")
    fun log(@RequestBody message: String): Single<String> {
        return Single.just(message)
            .doOnSuccess { log.info("Received Log: $it") }
            .map { "Log Processed" }
    }
}