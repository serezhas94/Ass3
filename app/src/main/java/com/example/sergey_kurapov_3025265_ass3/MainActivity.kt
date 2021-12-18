package com.example.sergey_kurapov_3025265_ass3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private var isStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get reference to the Tracker button
        val btnTracker = findViewById<Button>(R.id.btnTracker)
        // set on click listener
        btnTracker.setOnClickListener {
            // set value opposite to previous, started - stopped
            isStarted = !isStarted

            if(isStarted){
                // reset text
                btnTracker.setText(R.string.btn_tracker_stop)
                // start monitor activity
               // monitorActivity()
            }
            else{
                // reset text
                btnTracker.setText(R.string.btn_tracker_start)

                // open custom view
                val reportActivity: Intent = Intent(this, ReportActivity::class.java)
                this.startActivity(reportActivity)
            }
        }
    }

    private fun monitorActivity() {
        TODO("Not yet implemented")
    }
}