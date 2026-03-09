package com.sd.lab3.services
import com.sd.lab3.interfaces.WeatherForecastInterface
import com.sd.lab3.pojo.WeatherForecastData
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URL
import kotlin.math.roundToInt

@Service
class WeatherForecastService (private val timeService: TimeService) : WeatherForecastInterface {
    override fun getForecastData(locationId: Pair<Double, Double>): WeatherForecastData {
        val lat = locationId.first
        val lng = locationId.second
        val forecastDataURL = URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m&daily=temperature_2m_max,temperature_2m_min&timezone=auto")

        val rawResponse: String = forecastDataURL.readText()
        val responseRootObject = JSONObject(rawResponse)

        val current = responseRootObject.getJSONObject("current")
        val daily = responseRootObject.getJSONObject("daily")

        val weatherCode = current.getInt("weather_code")
        val weatherStateDescription = mapWmoCodeToDescription(weatherCode)

        return WeatherForecastData(
            location = "Lat: $lat, Lng: $lng",
            date = timeService.getCurrentTime(),
            weatherState = weatherStateDescription,
            weatherStateIconURL = "https://openweathermap.org/img/wn/01d@2x.png",
            windDirection = decodeWindDirection(current.getDouble("wind_direction_10m")),
            windSpeed = current.getDouble("wind_speed_10m").roundToInt(),
            minTemp = daily.getJSONArray("temperature_2m_min").getDouble(0).roundToInt(),
            maxTemp = daily.getJSONArray("temperature_2m_max").getDouble(0).roundToInt(),
            currentTemp = current.getDouble("temperature_2m").roundToInt(),
            humidity = current.getInt("relative_humidity_2m")
        )
    }

    private fun mapWmoCodeToDescription(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Mainly clear / Partly cloudy"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rain"
            71, 73, 75 -> "Snow fall"
            80, 81, 82 -> "Rain showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Unknown"
        }
    }

    private fun decodeWindDirection(degrees: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        return directions[((degrees % 360) / 45).roundToInt() % 8]
    }
}