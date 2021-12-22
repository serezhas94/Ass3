package com.example.sergey_kurapov_3025265_ass3

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    var pointsToDraw: ArrayList<Float> = ArrayList()

    private var graphPaint: Paint = Paint()
    private var axisPaint:Paint = Paint()
    private var pointAxisPaint:Paint = Paint()

    private val letterPaint = Paint()

    private val offset = 20.0f

    init {

        graphPaint.color = Color.RED
        graphPaint.strokeWidth = 10f

        axisPaint.color = Color.DKGRAY
        axisPaint.strokeWidth = 5f

        pointAxisPaint.color = Color.DKGRAY
        pointAxisPaint.strokeWidth = 10f

        letterPaint.style = Paint.Style.FILL
        letterPaint.color = Color.DKGRAY

    }

    fun setGraphArray(gpxDocument: GPXDocument) {

        for (point in gpxDocument.gpsPoints) {
            // add speed in km/h to graphArray
            val speedInKmH = point.speed * 3.6f
            pointsToDraw.add(speedInKmH)
        }

        // for test purpose only in simulated mode
        /*for(i in 1 until 11){
             pointsToDraw.add( i * 1.0f )
        }*/
    }

    // overridden draw function
    override fun onDraw(canvas: Canvas?) {
        // call the superclass drawing function before we start
        super.onDraw(canvas)

        // get the width and height of the canvas which is the available drawing area we have
        val widthCanvas = canvas!!.width.toFloat()
        val heightCanvas = canvas!!.height.toFloat()

        // fill the entire canvas
       canvas.drawColor(Color.LTGRAY)

        // draw X Axis
        canvas.drawLine(
            offset, heightCanvas - offset, widthCanvas - offset, heightCanvas- offset,
            axisPaint)

        // draw Y Axis
        canvas.drawLine(offset, offset, offset, heightCanvas - offset,
            axisPaint)

        // calculate scale of Y Axis -> max 10 km/h, offset taken twice from each side of Canvas
        val scaleY = (heightCanvas - 2 * offset) / 10

        // draw y scale points on Y axis
        for( i in 1 until 10){
            val scalePointY = (heightCanvas - (2 * offset)) - (i * scaleY)
            canvas.drawPoint(offset, scalePointY + offset , pointAxisPaint)
        }

        // draw the text
        letterPaint.textSize = 2 * offset
        canvas.drawText("Speed in km/h", 2 * offset, 4 * offset, letterPaint)

        // draw graph if data available
        if(pointsToDraw.size > 0){

            // calculate scale of Y Axis, offset taken twice from each side of Canvas
            val scaleX = (widthCanvas - 2 * offset)/ pointsToDraw.size

            // draw x scale points on X axis
            for( i in 1 until pointsToDraw.size){
                canvas.drawPoint( (scaleX * i)  + offset, heightCanvas - offset , pointAxisPaint)
            }

            // draw each point on graph
            for (i in 0 until pointsToDraw.size) {

                // to draw correctly on graph as coordinate system in canvas from top left
                val pointY = (heightCanvas - (2 * offset)) - (pointsToDraw[i] * scaleY)
                canvas.drawPoint(scaleX * i + offset, pointY + offset , graphPaint)
            }
        }
    }

    // overridden function touch events on oview
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}