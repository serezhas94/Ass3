package com.example.sergey_kurapov_3025265_ass3
import java.io.Serializable
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.atan2

class GPXDocument : Serializable{

    private val xmlHeader = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>"

     val gpsPoints: ArrayList<GPSUnit> = ArrayList()

    fun  addGPSPoint(gpsPoint: GPSUnit){
        gpsPoints.add(gpsPoint)
    }

    fun removeGPSPoint(gpsPoint: GPSUnit){
        gpsPoints.remove(gpsPoint)
    }

    // get average speed
    fun getAverageSpeed():Float{
        var sumAllSpeeds = 0.0f
        var count = 0
        for(point in gpsPoints) {
            sumAllSpeeds += point.speed
            count ++
        }
        var averageSpeed = 0.0f;
        if(count != 0)
            averageSpeed = sumAllSpeeds / count

        return averageSpeed
    }

    // get total time taken
    fun getTimeTaken(): Long {

        var totalTime = 0L
        if(gpsPoints.size > 1)
            totalTime = gpsPoints.last().unitTime.time.minus(gpsPoints.first().unitTime.time)

        return totalTime
    }

    // get min  altitude
    fun getMinAltitude():Double{
        var min = 0.0
        if(gpsPoints.size > 0)
            min = gpsPoints.first().altitude
        for(point in gpsPoints) {
            if (min > point.altitude )
                min = point.altitude
        }
        return min
    }

    // get max altitude
    fun getMaxAltitude():Double{
        var max = 0.0
        if(gpsPoints.size > 0)
            max = gpsPoints.first().altitude
        for(point in gpsPoints) {
            if (max < point.altitude )
                max = point.altitude
        }
        return max
    }

    fun getTotalDistance():Double{
        var distance = 0.0
        if(gpsPoints.size > 1) {
            val lat1 = gpsPoints.first().latitude
            val lon1 = gpsPoints.first().longitude

            val lat2 = gpsPoints.last().latitude
            val lon2 = gpsPoints.last().longitude

            // formula to calculate distance, taken from https://www.movable-type.co.uk/scripts/latlong.html
            val R = 6371e3; // metres
            val φ1 = lat1 * Math.PI / 180; // φ, λ in radians
            val φ2 = lat2 * Math.PI / 180;
            val Δφ = (lat2 - lat1) * Math.PI / 180;
            val Δλ = (lon2 - lon1) * Math.PI / 180;

            val a = sin(Δφ / 2) * sin(Δφ / 2) +
                    cos(φ1) * cos(φ2) *
                    sin(Δλ / 2) * sin(Δλ / 2);
            val c = 2 * atan2(sqrt(a), sqrt(1 - a));

            distance = R * c; // in metres
        }
        return distance

    }
    // get as XML string
    fun toXmlString(): String{

        var doc = xmlHeader
        doc += "<trk><trkseg>"
        for( point in gpsPoints){
            doc += "<trkpt lat= '" + point.latitude + "' lon='" + point.longitude +"'>"
            doc += "<altitude>" + point.altitude + "</altitude>"
            doc+= "<speed>" + point.speed + "</speed>"
            doc += "<time>" + point.unitTime.toString() + "</time>"
            doc += "</trkpt>"
        }
        doc += "</trkseg></trk>"

        return doc

    }
}