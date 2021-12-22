package com.example.sergey_kurapov_3025265_ass3

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle

import android.os.Looper
import android.provider.MediaStore

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*
import android.os.Environment

import java.io.*


class MainActivity : AppCompatActivity() {
    private var isStarted: Boolean = false
    private val requestPermissionLocation = 10

    private var locationProviderClient: FusedLocationProviderClient? = null

    private
    lateinit var locationRequest: LocationRequest

    // update interval 5 sec
    private val appInterval: Long = 5000
    private val appFastestInterval: Long = 5000

    private var gpxDocument: GPXDocument? = null
    private val subFolder = "GPSTracs"
    private val mimeType = "files/xml"

    // variable object Location Callback which extends LocationCallback class
    // and overrides onLocationResult method
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

                onLocationChanged(locationResult.lastLocation)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get reference to the Tracker button
        val btnTracker = findViewById<Button>(R.id.btnTracker)
        // set on click listener
        btnTracker.setOnClickListener {

            if (!isStarted) {

                // start monitor activity
                if (checkPermissionForLocation(this)) {

                    // tracker started
                    isStarted = true

                    // reset  button text
                    btnTracker.setText(R.string.btn_tracker_stop)

                    // start location Callback
                    startTracker()
                }

            } else if (isStarted) {

                // reset it
                btnTracker.setText(R.string.btn_tracker_start)
                isStarted = false

                // remove location Callback
                stopTracker()

                // write gpx document to folder
                writeFile()

                // open custom view
                val reportActivity = Intent(this, ReportActivity::class.java)

                // pass gpxDocument data to report activity
                reportActivity.putExtra("gpxDocument", gpxDocument)

                this.startActivity(reportActivity)
            }
        }
    }

    private fun checkPermissionForLocation(context: Context): Boolean {

        // check permissions - added in Manifest
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    requestPermissionLocation
                )
                false
            }
        } else {
            true
        }
    }

    fun onLocationChanged(location: Location) {

        // get location properties and set as GPSUnit
        val gpsUnit =
            GPSUnit(location.latitude, location.longitude, location.altitude, location.speed)

        if (gpxDocument?.gpsPoints?.size!! > 0) {

            val lastPoint = gpxDocument?.gpsPoints!!.last()
            if (gpsUnit.latitude == lastPoint.latitude && gpsUnit.longitude == lastPoint.longitude) {
                // same record exits - remove last one and add updated one after if
                gpxDocument?.removeGPSPoint(gpxDocument?.gpsPoints!!.last())
            }
        }

        // add record to GPX document
        gpxDocument?.addGPSPoint(gpsUnit)

    }

    private fun startTracker() {
        // Create the location request to start receiving updates
        locationRequest = LocationRequest.create().apply {
            interval = appInterval
            fastestInterval = appFastestInterval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        // check location setting
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // create new GPX Document to write to data
        gpxDocument = GPXDocument()

        // start location Callback
        locationProviderClient!!.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    private fun stopTracker() {
        locationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    private fun writeFile() {

        // ask permissions read and write
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
        )

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )

        // write if data exist
        if (gpxDocument != null) {
            try {

                val relativeLocation =
                    Environment.DIRECTORY_DOCUMENTS + File.separator.toString() + subFolder

                // create a new file name with extension
                val date: Date = Calendar.getInstance().time
                val sdf = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")
                val fileName = sdf.format(date)
                val displayName = "$fileName.xml"

                // depends on version it runs
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    // by using content values, content resolver and file descriptor
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                    contentValues.put(MediaStore.Video.Media.TITLE, fileName)
                    contentValues.put(
                        MediaStore.Video.Media.DATE_ADDED,
                        System.currentTimeMillis() / 1000
                    )
                    contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())

                    val resolver = this.contentResolver
                    val contentUri = MediaStore.Files.getContentUri("external")
                    val uri = resolver.insert(contentUri, contentValues)

                    // open file descriptor
                    val xmlDesc = contentResolver.openFileDescriptor(uri!!, "w")

                    // open file output stream
                    val out = FileOutputStream(xmlDesc!!.fileDescriptor)
                    val outWriter = OutputStreamWriter(out)

                    // write to file data
                    outWriter.write(gpxDocument?.toXmlString())

                    // close streams
                    outWriter.close()
                    out.close()
                    xmlDesc.close()

                } else {

                    // older versions - get external path dir
                    val fileDir = Environment.getExternalStoragePublicDirectory(relativeLocation)
                        .toString()

                    // create new file
                    val gpsFile = File(fileDir, displayName)

                    if (!gpsFile.exists()) {
                        gpsFile.createNewFile()

                        gpsFile.setWritable(true)
                        gpsFile.setReadable(true)
                    }

                    // create file output stream
                    val out = FileOutputStream(gpsFile)

                    // create write stream
                    val outWriter = OutputStreamWriter(out)

                    // write to file data
                    outWriter.write(gpxDocument?.toXmlString())

                    // close streams
                    outWriter.close()
                    out.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}