package com.sd.lab3.services

import com.sd.lab3.interfaces.LocationSearchInterface
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class LocationSearchService : LocationSearchInterface {
    override fun getLocationId(locationName: String): Pair<Double, Double> {
        // 1. Build the URL
        val encodedLocationName = URLEncoder.encode(locationName, StandardCharsets.UTF_8.toString())
        val locationSearchURL = URL("https://geocoding-api.open-meteo.com/v1/search?name=$encodedLocationName&count=1&language=en&format=json")

        // 2. Read response
        val rawResponse: String = locationSearchURL.readText()

        // 3. Parse JSON
        val responseRootObject = JSONObject(rawResponse)

        if (responseRootObject.has("results")) {
            val firstResult = responseRootObject.getJSONArray("results").getJSONObject(0)
            val lat = firstResult.getDouble("latitude")
            val lng = firstResult.getDouble("longitude")
            return Pair(lat, lng)
        } else {
            throw Exception("bad location")
        }


    }
}