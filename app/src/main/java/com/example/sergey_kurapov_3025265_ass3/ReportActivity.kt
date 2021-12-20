package com.example.sergey_kurapov_3025265_ass3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView


class ReportActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val gpxDocument: GPXDocument = intent.getSerializableExtra("gpxDocument") as GPXDocument

        // set text values on text views
        val txtAverageSpeed = findViewById<TextView>(R.id.txtAvSpeed)
        (txtAverageSpeed.text.toString() + " " + gpxDocument.getAverageSpeed()).also { txtAverageSpeed.text = it }

        val txtTotalDistance = findViewById<TextView>(R.id.txtTotalDistance)
        (txtTotalDistance.text.toString() + " " + gpxDocument.getTotalDistance() + " in metres").also { txtTotalDistance.text = it }

        val timeTaken = gpxDocument.getTimeTaken()/ 1000.0 // in seconds
        val txtTimeTaken = findViewById<TextView>(R.id.txtTimeTaken)
        (txtTimeTaken.text.toString() + " " + timeTaken + " seconds").also { txtTimeTaken.text = it }

        val txtMinAltitude = findViewById<TextView>(R.id.txtMinAltitude)
        (txtMinAltitude.text.toString() + " " + gpxDocument.getMinAltitude()).also { txtMinAltitude.text = it }

        val txtMaxAltitude = findViewById<TextView>(R.id.txtMaxAltitude)
        (txtMaxAltitude.text.toString() + " " + gpxDocument.getMaxAltitude()).also { txtMaxAltitude.text = it }

    }
}