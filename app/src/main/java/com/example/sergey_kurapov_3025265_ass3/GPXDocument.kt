package com.example.sergey_kurapov_3025265_ass3

class GPXDocument {

    private val xmlHeader = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>"

    private val gpsPoints: ArrayList<GPSUnit> = ArrayList()

    fun  addGPSPoint(gpsPoint: GPSUnit){
        gpsPoints.add(gpsPoint)
    }

    fun toXmlString(): String{

        var doc = xmlHeader
        doc += "<trk><trkseg>"
        for( point in gpsPoints){
            doc += "<trkpt lat= '" + point.latitude + "' lon='" + point.latitude +"'>"
            doc += "<altitude>" + point.altitude + "</altitude>"
            doc+= "<speed>" + point.speed + "</speed>"
            doc += "<time>" + point.unitTime.toString() + "</time>"
            doc += "</trkpt>"
        }
        doc += "</trkseg></trk>"

        return doc

    }
}