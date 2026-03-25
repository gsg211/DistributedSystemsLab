import hashlib
import os
from flask import Flask, jsonify, request
from cryptography.fernet import Fernet

app = Flask(__name__)

ENCRYPTION_KEY = os.getenv("KEY")
cipher_suite = Fernet(ENCRYPTION_KEY)

class CryptographyManager:
    @staticmethod
    def hash_input(data: str) -> str:
        return hashlib.sha256(data.encode()).hexdigest()

    @staticmethod
    def encrypt_input(data: str) -> str:
        return cipher_suite.encrypt(data.encode()).decode()

    @staticmethod
    def decrypt_input(data: str) -> str:
        return cipher_suite.decrypt(data.encode()).decode()


@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "up", "service": "crypto"}), 200

@app.route('/hash', methods=['POST'])
def hash_route():
    data = request.json.get('input', '')
    return jsonify({"result": CryptographyManager.hash_input(data)})

@app.route('/encrypt', methods=['POST'])
def encrypt_route():
    data = request.json.get('input', '')
    return jsonify({"result": CryptographyManager.encrypt_input(data)})

@app.route('/decrypt', methods=['POST'])
def decrypt_route():
    data = request.json.get('input', '')
    try:
        return jsonify({"result": CryptographyManager.decrypt_input(data)})
    except Exception:
        return jsonify({"error": "Decryption failed"}), 400

if __name__ == '__main__':
   app.run(host='0.0.0.0', port=5001)