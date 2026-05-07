import json
import time
import random
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import NoBrokersAvailable

class Bidder:
    def __init__(self, bidder_id, bootstrap_servers):
        self.bidder_id = bidder_id
        self.bootstrap_servers = bootstrap_servers
        
        # Topics
        self.announcement_topic = "auction_events"  # Listen for START/UPDATE
        self.bids_topic = "bids_topic"             # Send bids here
        
        # Initialize Producer & Consumer with retry logic
        self.producer = self._create_producer()
        self.consumer = self._create_consumer()

    def _create_producer(self):
        print(f"[Bidder-{self.bidder_id}] Connecting Producer to Kafka at {self.bootstrap_servers}...")
        while True:
            try:
                producer = KafkaProducer(
                    bootstrap_servers=self.bootstrap_servers,
                    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                    request_timeout_ms=10000
                )
                print(f"[Bidder-{self.bidder_id}] Producer Connected!")
                return producer
            except NoBrokersAvailable:
                print(f"[Bidder-{self.bidder_id}] Producer waiting for Kafka... (retrying in 3s)")
                time.sleep(3)

    def _create_consumer(self):
        print(f"[Bidder-{self.bidder_id}] Connecting Consumer to Kafka at {self.bootstrap_servers}...")
        while True:
            try:
                consumer = KafkaConsumer(
                    self.announcement_topic,
                    bootstrap_servers=self.bootstrap_servers,
                    value_deserializer=lambda x: json.loads(x.decode('utf-8')),
                    group_id=f'bidder-group-{self.bidder_id}', # Unique group per bidder instance
                    auto_offset_reset='latest' # Bidders usually care about new auctions
                )
                print(f"[Bidder-{self.bidder_id}] Consumer Connected!")
                return consumer
            except NoBrokersAvailable:
                print(f"[Bidder-{self.bidder_id}] Consumer waiting for Kafka... (retrying in 3s)")
                time.sleep(3)

    def place_bid(self, auction_data):
        """Simple bidding logic: bid current price + random amount"""
        current_price = auction_data.get('start_cost', 0)
        bid_amount = current_price + random.uniform(1, 10)
        
        bid_payload = {
            "bidder_id": self.bidder_id,
            "auction_id": auction_data.get('auction_id', 'unknown'),
            "amount": round(bid_amount, 2),
            "timestamp": time.time()
        }
        
        print(f"[Bidder-{self.bidder_id}] Placing bid: {bid_payload['amount']}")
        self.producer.send(self.bids_topic, bid_payload)
        self.producer.flush()

    def run(self):
        print(f"[Bidder-{self.bidder_id}] Waiting for auctions...")
        try:
            for message in self.consumer:
                msg = message.value
                event_type = msg.get("event")
                data = msg.get("data")

                if event_type == "START":
                    print(f"[Bidder-{self.bidder_id}] New auction detected! Data: {data}")
                    # Logic to decide if this bidder wants to participate
                    self.place_bid(data)
                
                elif event_type == "UPDATE":
                    # Potentially bid again if price changed (for English/Dutch types)
                    print(f"[Bidder-{self.bidder_id}] Auction update received.")

        except Exception as e:
            print(f"[Bidder-{self.bidder_id}] Runtime error: {e}")

if __name__ == "__main__":
    import os
    KAFKA_SERVER = 'kafka:9092'
    # Use environment variable for ID if running multiple containers
    BIDDER_ID = os.getenv("BIDDER_ID", f"Bidder_{random.randint(1, 1000)}")
    
    service = Bidder(BIDDER_ID, KAFKA_SERVER)
    service.run()