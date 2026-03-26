import time
import os
from kombu import Connection

rabbit_host = os.environ.get('RABBITMQ_HOST', 'localhost')
url = f"amqp://guest:guest@{rabbit_host}:5672//"
menu={
    1:"BURGER",
    2:"PIZZA",
    3:"SALATA"
}

def send_task():
    print(f"[*] Conectare la RabbitMQ prin Kombu: {url}")

    try:
        with Connection(url) as conn:
            simple_queue = conn.SimpleQueue('comenzi')

            i = 1
            while True:
                print("1.BURGER\n2.PIZZA\n3.SALATA")
                id=int(input("MANCARE ID: >> "))

                simple_queue.put(menu[id])
                print(f" [>] Trimis: {menu[id]}")
                i += 1

    except Exception as e:
        print(f"Eroare: {e}. Reîncercăm în 2 secunde...")
        time.sleep(2)
        send_task()


if __name__ == "__main__":
    send_task()