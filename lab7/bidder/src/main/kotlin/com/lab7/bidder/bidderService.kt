package com.lab7.bidder



import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class BidderService(
    private val webClientBuilder: WebClient.Builder,
    private val remoteLogger: RemoteLogger
) {

    @Value("\${AUCTIONEER_URL}")
    private lateinit var auctioneerUrl: String


    @EventListener(ApplicationReadyEvent::class)
    fun startBiddingProcess() {
        val myId = "Bidder-${UUID.randomUUID().toString().take(4)}"
        val myAmount = (10..100).random()

        val bidData = mapOf(
            "bidderId" to myId,
            "amount" to myAmount
        )

        println("[$myId] Sending bid of $$myAmount to Auctioneer...")

        val responseMono = webClientBuilder.build()
            .post()
            .uri(auctioneerUrl)
            .bodyValue(bidData)
            .retrieve()
            .bodyToMono(String::class.java)

        // 2. Convert the Mono to an RxJava Single
        // We use Single because we expect exactly ONE response back from the Auctioneer
        val resultSingle = Single.fromPublisher(responseMono)

        // 3. SUBSCRIBE: This is what actually sends the request and waits for the answer
        resultSingle.subscribeBy(
            onSuccess = { result ->
                remoteLogger.info("[$myId] AUCTION FINISHED! Result: $result")
            },
            onError = { error ->
                // This code runs if the Auctioneer is down or a timeout occurs
                remoteLogger.info("[$myId] Auction failed: ${error.message}")
            }
        )

        remoteLogger.info("[$myId] Bid sent. Waiting for the auctioneer to finish the round...")
    }
}