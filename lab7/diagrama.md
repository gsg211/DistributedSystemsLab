# Diagrama

```mermaid
---
title: Service Diagram
---
classDiagram
class Bidder{
    + bid()
    + waitForResult()
}

class Auctioneer{
    +receiveBids()
    +forwardBids()
    +finishAuction()
}

Bidder -->Auctioneer

Auctioneer --> Bidder

```
