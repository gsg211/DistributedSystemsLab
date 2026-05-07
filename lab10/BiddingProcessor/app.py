import json
import time
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import NoBrokersAvailable
# Assumes your strategy file is named auctions.py
from auctions import AuctionStrategyFactory

class BiddingProcessor:
    def __init__(self, bootstrap_servers):
        self.bootstrap_servers = bootstrap_servers
        self.processed_bids_topic = "processed_bids_topic"
        self.status_topic = "status_topic"

        # Current State
        self.current_auction = None
        self.received_bids = []

        # 1. Initialize Producer with retry logic
        self.producer = self._create_producer()

        # 2. Initialize Consumer with retry logic
        self.consumer = self._create_consumer()

    def _create_producer(self):
        print(f"[BiddingProcessor] Connecting Producer to Kafka at {self.bootstrap_servers}...")
        while True:
            try:
                producer = KafkaProducer(
                    bootstrap_servers=self.bootstrap_servers,
                    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                    request_timeout_ms=10000
                )
                print("[BiddingProcessor] Producer Connected!")
                return producer
            except NoBrokersAvailable:
                print("[BiddingProcessor] Producer waiting for Kafka... (retrying in 3s)")
                time.sleep(3)

    def _create_consumer(self):
        print(f"[BiddingProcessor] Connecting Consumer to Kafka at {self.bootstrap_servers}...")
        while True:
            try:
                consumer = KafkaConsumer(
                    self.processed_bids_topic,
                    bootstrap_servers=self.bootstrap_servers,
                    value_deserializer=lambda x: json.loads(x.decode('utf-8')),
                    group_id='bidding-processor-group',
                    auto_offset_reset='latest',
                    # Crucial: allows the loop to break if no messages arrive
                    # so we can check the 20-second timer
                    consumer_timeout_ms=1000
                )
                print("[BiddingProcessor] Consumer Connected!")
                return consumer
            except NoBrokersAvailable:
                print("[BiddingProcessor] Consumer waiting for Kafka... (retrying in 3s)")
                time.sleep(3)

    def decide_auction_winner(self):
        if not self.current_auction or not self.received_bids:
            print("[BiddingProcessor] No bids received or no active auction. Resetting.")
            self.current_auction = {"type": "English", "start_cost": 100} # Keep demo alive
            return

        try:
            strategy = AuctionStrategyFactory.create_strategy(self.current_auction['type'])
            winner = strategy.determine_winner(self.received_bids)

            result = {
                "status": "FINISHED",
                "type": self.current_auction['type'],
                "winner": winner['bidder_id'] if winner else "None",
                "final_cost": winner['amount'] if winner else 0,
                "auction_id": self.current_auction.get('auction_id', 'demo_id')
            }

            print(f"[BiddingProcessor] Winner determined: {result}")
            self.producer.send(self.status_topic, result)
            self.producer.flush()
        except Exception as e:
            print(f"[BiddingProcessor] Strategy Error: {e}")

        # Reset for next auction
        self.received_bids = []
        # In real logic, we'd wait for a new START event,
        # but for your demo we reset the current_auction placeholder
        self.current_auction = {"type": "English", "start_cost": 100}

    def run(self):
        print("[BiddingProcessor] Orchestrator running...")
        start_time = time.time()

        # Simulare: definim o licitație activă
        self.current_auction = {"type": "English", "start_cost": 100}

        while True:
            try:
                # We iterate over the consumer with a timeout
                for message in self.consumer:
                    bid = message.value
                    print(f"[BiddingProcessor] Analyzing bid from {bid['bidder_id']}: ${bid['amount']}")
                    self.received_bids.append(bid)

                    # Check if we should end based on bid count
                    if len(self.received_bids) >= 3:
                        self.decide_auction_winner()
                        start_time = time.time()

                # This part is reached every 1 second (consumer_timeout_ms)
                # Check if we should end based on time
                if time.time() - start_time > 20:
                    print("[BiddingProcessor] Time limit reached for current auction.")
                    self.decide_auction_winner()
                    start_time = time.time()

            except Exception as e:
                print(f"[BiddingProcessor] Runtime loop error: {e}")
                time.sleep(1)

if __name__ == "__main__":
    KAFKA_SERVER = 'kafka:9092'
    # Class handles its own waiting logic
    processor = BiddingProcessor(KAFKA_SERVER)
    processor.run()