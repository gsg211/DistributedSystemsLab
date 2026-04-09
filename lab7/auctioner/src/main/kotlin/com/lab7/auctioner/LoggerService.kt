package com.lab7.auctioner


import io.reactivex.rxjava3.core.Completable
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class RemoteLogger(private val webClientBuilder: WebClient.Builder) {

    @Value("\${LOGGER_URL:http://logger:8080/log}")
    private lateinit var loggerUrl: String

    private val client by lazy { webClientBuilder.build() }

    fun info(message: String) {

        println("[LOCAL] $message")


        client.post()
            .uri(loggerUrl)
            .bodyValue(message)
            .retrieve()
            .toBodilessEntity()
            .subscribe(
                { /* successfully sent to logger */ },
                { err -> println("[ERR] Could not send to remote logger: ${err.message}") }
            )
    }
}