package com.example.sergey_kurapov_3025265_ass3

import java.io.Serializable
import java.util.*

class GPSUnit(lat:Double,lon: Double, alt: Double, speed: Float ): Serializable {

    val unitTime: Date = Calendar.getInstance().time
    val latitude: Double = lat
    val longitude:Double = lon
    val altitude: Double =alt
    val  speed: Float = speed
}