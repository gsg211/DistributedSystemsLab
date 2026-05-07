import json
import threading
import time
from flask import Flask, render_template, request, jsonify
from kafka import KafkaProducer, KafkaConsumer
from kafka.errors import NoBrokersAvailable

class AuctionManager:
    def __init__(self, auctions_topic, result_topic, bootstrap_servers):
        self.auctions_topic = auctions_topic
        self.result_topic = result_topic
        self.bootstrap_servers = bootstrap_servers
        self.results = []
        self._stop_event = threading.Event()

        # 1. Initialize the producer with retry logic
        self.auctions_producer = self._create_producer()

        # 2. Start the consumer thread
        self.consumer_thread = threading.Thread(target=self._listen_for_results)
        self.consumer_thread.daemon = True
        self.consumer_thread.start()

    def _create_producer(self):
        """Attempts to connect to Kafka until successful."""
        print(f"[UI] Attempting to connect to Kafka Producer at {self.bootstrap_servers}...")
        while True:
            try:
                producer = KafkaProducer(
                    bootstrap_servers=self.bootstrap_servers,
                    value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                    request_timeout_ms=5000,
                    # Retries inside the producer itself
                    retries=5
                )
                print("[UI] Successfully connected to Kafka Producer!")
                return producer
            except NoBrokersAvailable:
                print("[UI] Kafka broker not available yet. Retrying in 2 seconds...")
                time.sleep(2)
            except Exception as e:
                print(f"[UI] Unexpected error connecting to Kafka: {e}")
                time.sleep(2)

    def _listen_for_results(self):
        """Consumer loop that runs in a background thread."""
        consumer = None
        # Try to connect the consumer
        while consumer is None:
            try:
                consumer = KafkaConsumer(
                    self.result_topic,
                    bootstrap_servers=self.bootstrap_servers,
                    value_deserializer=lambda x: json.loads(x.decode('utf-8')),
                    auto_offset_reset='earliest',
                    group_id='ui-group',
                    consumer_timeout_ms=1000  # Allows checking the stop_event
                )
                print("[UI] Successfully connected to Kafka Consumer!")
            except NoBrokersAvailable:
                time.sleep(2)

        # Start polling for messages
        while not self._stop_event.is_set():
            try:
                for message in consumer:
                    if self._stop_event.is_set():
                        break
                    result = message.value
                    print(f"[UI] Rezultat primit: {result}")
                    self.results.append(result)
            except Exception as e:
                # If consumer fails mid-run, it will log and the loop continues
                print(f"[UI] Consumer error: {e}")
                time.sleep(1)

    def new_Auction(self, start_cost, auction_type):
        """Sends a new auction event to Kafka."""
        auction_data = {
            "start_cost": start_cost,
            "type": auction_type,
            "status": "STARTING",
            "timestamp": time.time()
        }
        try:
            self.auctions_producer.send(self.auctions_topic, auction_data)
            self.auctions_producer.flush()
            print(f"[UI] Sent auction to Kafka: {auction_data}")
            return auction_data
        except Exception as e:
            print(f"[UI] Failed to send auction: {e}")
            return {"status": "error", "message": str(e)}

# --- Flask App Setup ---

app = Flask(__name__)
KAFKA_SERVER = 'kafka:9092'

# We initialize the manager here.
# Note: This will block the start of Flask until the Producer is connected.
auction_manager = AuctionManager("auctions_topic", "result_topic", KAFKA_SERVER)

@app.route('/')
def index():
    return render_template('index.html', results=auction_manager.results)

@app.route('/create_auction', methods=['POST'])
def create_auction():
    data = request.form
    try:
        start_cost = float(data.get('start_cost', 0))
        auction_type = data.get('type', 'English')
        auction_manager.new_Auction(start_cost, auction_type)
        return jsonify({"status": "Auction sent to Kafka", "data": data})
    except ValueError:
        return jsonify({"status": "error", "message": "Invalid start cost"}), 400

@app.route('/get_results')
def get_results():
    return jsonify(auction_manager.results)

if __name__ == '__main__':
    # host='0.0.0.0' is required for Docker access
    app.run(host='0.0.0.0', port=5000, debug=False)