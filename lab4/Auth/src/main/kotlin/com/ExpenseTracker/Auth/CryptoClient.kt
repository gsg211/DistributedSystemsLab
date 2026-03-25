package com.ExpenseTracker.Auth

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CryptoClient(private val restTemplate: RestTemplate) {
    private val cryptoUrl = "http://crypto-service:8080"

    fun getHash(password: String): String {
        val request = mapOf("input" to password)
        val response = restTemplate.postForObject("$cryptoUrl/hash", request, Map::class.java)
        return response?.get("result") as String
    }

    fun encrypt(data: String): String {
        val request = mapOf("input" to data)
        val response = restTemplate.postForObject("$cryptoUrl/encrypt", request, Map::class.java)
        return response?.get("result") as String
    }

    fun decrypt(data: String): String {
        val request = mapOf("input" to data)
        val response = restTemplate.postForObject("$cryptoUrl/decrypt", request, Map::class.java)
        return response?.get("result") as String
    }
}