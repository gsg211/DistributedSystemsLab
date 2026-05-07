import json
import time
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import NoBrokersAvailable

class MessageProcessor:
    def __init__(self, bootstrap_servers):
        self.bootstrap_servers = bootstrap_servers
        # Topic-uri conform diagramei
        self.bids_topic = "bids_topic"
        self.processed_bids_topic = "processed_bids_topic"

        # 1. Initialize Producer with retry logic
        self.producer = self._create_producer()

        # 2. Initialize Consumer with retry logic
        self.consumer = self._create_consumer()

    def _create_producer(self):
        """Infinite loop until the producer can reach Kafka."""
        print(f"[MessageProcessor] Connecting Producer to Kafka at {self.bootstrap_servers}...")
        while True:
            try:
                producer = KafkaProducer(
                    bootstrap_servers=self.bootstrap_servers,
                    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                    request_timeout_ms=10000
                )
                print("[MessageProcessor] Producer Connected!")
                return producer
            except NoBrokersAvailable:
                print("[MessageProcessor] Producer waiting for Kafka... (retrying in 3s)")
                time.sleep(3)

    def _create_consumer(self):
        """Infinite loop until the consumer can reach Kafka."""
        print(f"[MessageProcessor] Connecting Consumer to Kafka at {self.bootstrap_servers}...")
        while True:
            try:
                consumer = KafkaConsumer(
                    self.bids_topic,
                    bootstrap_servers=self.bootstrap_servers,
                    value_deserializer=lambda x: json.loads(x.decode('utf-8')),
                    group_id='message-processor-group',
                    auto_offset_reset='earliest'
                )
                print("[MessageProcessor] Consumer Connected!")
                return consumer
            except NoBrokersAvailable:
                print("[MessageProcessor] Consumer waiting for Kafka... (retrying in 3s)")
                time.sleep(3)

    def process_message(self, raw_bid):
        """
        Validare și curățare (Exemplu: Verificăm dacă are câmpurile necesare)
        """
        required_fields = ['bidder_id', 'amount', 'timestamp']
        try:
            if all(field in raw_bid for field in required_fields):
                return {
                    "bidder_id": raw_bid['bidder_id'],
                    "amount": float(raw_bid['amount']),
                    "timestamp": raw_bid['timestamp'],
                    "valid": True
                }
        except (ValueError, TypeError) as e:
            print(f"[MessageProcessor] Error parsing data types: {e}")
        return None

    def finish_processing(self, processed_bid):
        """Trimite mesajul curat către BiddingProcessor."""
        try:
            self.producer.send(self.processed_bids_topic, processed_bid)
            self.producer.flush()
            print(f"[MessageProcessor] Mesaj procesat trimis: {processed_bid}")
        except Exception as e:
            print(f"[MessageProcessor] Failed to send processed bid: {e}")

    def run(self):
        print("[MessageProcessor] Worker pornit. Ascult pentru bids...")
        try:
            for message in self.consumer:
                raw_bid = message.value
                print(f"[MessageProcessor] Received raw bid from: {raw_bid.get('bidder_id')}")

                clean_bid = self.process_message(raw_bid)

                if clean_bid:
                    self.finish_processing(clean_bid)
                else:
                    print(f"[MessageProcessor] Invalid message ignored: {raw_bid}")
        except Exception as e:
            print(f"[MessageProcessor] Runtime error in main loop: {e}")

if __name__ == "__main__":
    KAFKA_SERVER = 'kafka:9092'
    # The class now handles the waiting, so we can start immediately
    processor = MessageProcessor(KAFKA_SERVER)
    processor.run()