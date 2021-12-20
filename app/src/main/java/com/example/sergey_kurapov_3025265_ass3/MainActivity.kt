package com.example.sergey_kurapov_3025265_ass3

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.os.Looper
import android.provider.MediaStore

import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import com.google.android.gms.location.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import android.os.Environment





class MainActivity : AppCompatActivity() {
    private var isStarted: Boolean = false
    private val requestPermissionLocation = 10

    private var locationProviderClient: FusedLocationProviderClient? = null
    private
    lateinit var locationRequest: LocationRequest

    // update interval 5 sec
    private val appInterval: Long = 5000
    private val appFastestInterval: Long = 5000

    private  var gpxDocument: GPXDocument? = null

    private val externalUri: Uri = MediaStore.Files.getContentUri("external")
    private val filesPath = "Documents"
    private val displayName = "GPSTracs"
    private val filesFormat = "application/xml"

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

            if(!isStarted){

                // start monitor activity
                if (checkPermissionForLocation(this)) {

                    // tracker started
                    isStarted = true

                    // reset  button text
                    btnTracker.setText(R.string.btn_tracker_stop)

                    // start location Callback
                    startTracker()
                }

            }
            else if(isStarted){

                // reset it
                btnTracker.setText(R.string.btn_tracker_start)
                isStarted = false

                // remove location Callback
                stopTracker()

                // write gpx document to folder
                writeFile()

                // open custom view
                val reportActivity = Intent(this, ReportActivity::class.java)
                this.startActivity(reportActivity)
            }
        }
    }
    private fun checkPermissionForLocation(context: Context): Boolean {

        // check permissions - added in Manifest
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
                true
            }else{
                // Show the permission request
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    requestPermissionLocation )
                false
            }
        } else {
            true
        }
    }

    fun onLocationChanged(location: Location) {

        // get location properties and set as GPSUnit
        val gpsUnit = GPSUnit(location.latitude,location.longitude, location.altitude, location.speed )

        // add record to GPX document
        gpxDocument?.addGPSPoint(gpsUnit)
    }

    private fun startTracker() {
        // Create the location request to start receiving updates
        locationRequest = LocationRequest.create().apply {
            interval = appInterval
            fastestInterval = appFastestInterval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 100
        }

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest!!)
        val locationSettingsRequest = builder.build()

        // check location setting
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // create new GPX Document to write to data
        gpxDocument = GPXDocument()

        // start location Callback
        locationProviderClient!!.requestLocationUpdates(locationRequest, locationCallback,
            Looper.myLooper())
    }

    private fun stopTracker() {
        locationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    private fun writeFile() {

        // ask permissions read and write
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
        );

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        );

        // write if data exist
        if (gpxDocument != null) {
            try {

                // get external directory
                val resolver = this.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    //put(MediaStore.MediaColumns.MIME_TYPE, filesFormat)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, filesPath)
                }
                val uri: Uri? = resolver.insert(externalUri, contentValues)

                val path = getRealPathFromURI(uri)
                val fileDir = File(path)

                // create external directory if doesn't exist
                if (!fileDir.exists()) {
                    fileDir.mkdirs()
                }

                // create a new file if doesn't exist
                val date: Date = Calendar.getInstance().time
                val sdf = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")
                val fileName = sdf.format(date)
                val gpsFile = File("$path/$fileName.txt")

                if (!gpsFile.exists()) {
                    gpsFile.createNewFile()

                    gpsFile.setWritable(true)
                    gpsFile.setReadable(true)
                }

                // create file output stream
                val fOut = FileOutputStream(gpsFile)
                // create write stream
                val outWriter = OutputStreamWriter(fOut)

                // write to file data
                outWriter.write(gpxDocument?.toXmlString())

                // close streams
                outWriter.close()
                fOut.close()


            } catch (e: Exception) {
                e.printStackTrace();
            }
        }
    }
    private fun getRealPathFromURI(contentURI: Uri?): String? {
        val result: String?
        val cursor: Cursor? = contentResolver.query(contentURI!!, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI!!.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

}