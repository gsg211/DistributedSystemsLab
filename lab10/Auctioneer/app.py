import json
import time
import threading
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import NoBrokersAvailable


class Auctioneer:
    def __init__(self, bootstrap_servers):
        self.bootstrap_servers = bootstrap_servers
        self.auctions_topic = "auctions_topic"
        self.status_topic = "status_topic"
        self.result_topic = "result_topic"
        self.bidder_announcement = "auction_events"

        # Initialize Producer with retry logic
        self.producer = self._create_producer()

        # Initialize Consumer with retry logic
        self.consumer = self._create_consumer()

    def _create_producer(self):
        print(f"[Auctioneer] Connecting Producer to Kafka at {self.bootstrap_servers}...")
        while True:
            try:
                producer = KafkaProducer(
                    bootstrap_servers=self.bootstrap_servers,
                    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                    request_timeout_ms=10000
                )
                print("[Auctioneer] Producer Connected!")
                return producer
            except NoBrokersAvailable:
                print("[Auctioneer] Producer waiting for Kafka... (retrying in 3s)")
                time.sleep(3)

    def _create_consumer(self):
        print(f"[Auctioneer] Connecting Consumer to Kafka at {self.bootstrap_servers}...")
        while True:
            try:
                consumer = KafkaConsumer(
                    self.auctions_topic,
                    self.status_topic,
                    bootstrap_servers=self.bootstrap_servers,
                    value_deserializer=lambda x: json.loads(x.decode('utf-8')),
                    group_id='auctioneer-group',
                    auto_offset_reset='earliest'
                )
                print("[Auctioneer] Consumer Connected!")
                return consumer
            except NoBrokersAvailable:
                print("[Auctioneer] Consumer waiting for Kafka... (retrying in 3s)")
                time.sleep(3)

    def start_auction(self, auction_data):
        print(f"[Auctioneer] Pornesc licitație nouă: {auction_data}")
        self.producer.send(self.bidder_announcement, {
            "event": "START",
            "data": auction_data
        })
        self.producer.flush()

    def finish_auction(self, result_data):
        print(f"[Auctioneer] Finalizez licitația: {result_data}")
        self.producer.send(self.result_topic, result_data)
        self.producer.flush()

    def run(self):
        print("[Auctioneer] Worker pornit, aștept mesaje...")
        try:
            for message in self.consumer:
                topic = message.topic
                data = message.value

                if topic == self.auctions_topic:
                    self.start_auction(data)

                elif topic == self.status_topic:
                    if data.get("status") == "FINISHED":
                        self.finish_auction(data)
                    else:
                        print(f"[Auctioneer] Update stare: {data}")
                        self.producer.send(self.bidder_announcement, {
                            "event": "UPDATE",
                            "data": data
                        })
        except Exception as e:
            print(f"[Auctioneer] Runtime error: {e}")


if __name__ == "__main__":
    KAFKA_SERVER = 'kafka:9092'
    # No need for a blind time.sleep(10) anymore, the loops handle it.
    service = Auctioneer(KAFKA_SERVER)
    service.run()