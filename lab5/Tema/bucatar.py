import multiprocessing
import time
import os
from uuid import uuid4

from kombu import Connection

rabbit_host = os.environ.get('RABBITMQ_HOST', 'localhost')
url = f"amqp://guest:guest@{rabbit_host}:5672//"
cooks = 3

menu={
    "BURGER":5,
    "PIZZA":10,
    "SALATA":2
}

def start_worker():
    print(f"[*] bucatarie deschisa")
    cookid=uuid4()
    try:
        with Connection(url) as conn:
            with conn.SimpleQueue('comenzi') as queue:
                print(' [*] Aștept comenzi. Pentru ieșire apasă CTRL+C')

                while True:
                    message = queue.get(block=True)
                    food=message.body
                    print(f" [{cookid}] Comanda noua: {food}")
                    print(f" [{cookid}] Timp estimat: {menu[food]}")
                    time.sleep(menu[food])
                    print(f" [{cookid}] Gata!")

                    message.ack()

    except Exception as e:
        print(f"Eroare: {e}. Reîncercăm...")
        time.sleep(2)
        start_worker()


if __name__ == "__main__":
    try:
        processes = []
        for _ in range(cooks):
            p = multiprocessing.Process(target=start_worker)
            p.start()
            processes.append(p)

        try:
            for p in processes:
                p.join()
        except KeyboardInterrupt:
            print("\n[*] Închidem bucătăria...")
            for p in processes:
                p.terminate()
    except KeyboardInterrupt:
        print("\nOprit de utilizator.")