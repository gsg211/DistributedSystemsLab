package com.sd.lab3.interfaces

interface LocationSearchInterface {
    fun getLocationId(locationName: String): Pair<Double, Double> //coords
}