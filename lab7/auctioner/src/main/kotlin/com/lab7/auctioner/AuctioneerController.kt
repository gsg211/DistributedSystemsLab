package com.lab7.auctioner
import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit


import io.reactivex.rxjava3.subjects.PublishSubject
import jakarta.annotation.PostConstruct

@RestController
@RequestMapping("/auction")
class AuctioneerController( private val remoteLogger: RemoteLogger) {

    private val auctionResultSubject = PublishSubject.create<String>()

    private val currentBids = mutableListOf<Pair<String, Int>>()

    @PostConstruct
    fun startAuctionTimer() {
        io.reactivex.rxjava3.core.Observable.interval(15, TimeUnit.SECONDS)
            .subscribe {
                val winnerMessage = if (currentBids.isEmpty()) {
                    "Auction ended: No bids received."
                } else {
                    val winner = currentBids.maxByOrNull { it.second }
                    "Auction ended: Winner is ${winner?.first} with $${winner?.second}!"
                }

                remoteLogger.info(winnerMessage)
                auctionResultSubject.onNext(winnerMessage)


                currentBids.clear()
            }
    }

    @PostMapping("/bid")
    fun receiveBid(@RequestBody bid: Map<String, Any>): Single<String> {
        val bidderId = bid["bidderId"].toString()
        val amount = bid["amount"].toString().toInt()

        currentBids.add(bidderId to amount)
        remoteLogger.info("Received bid from $bidderId: $$amount")

        return auctionResultSubject.firstOrError()
    }
}