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

    var pointsX: ArrayList<Int> = ArrayList()
    var pointsY: ArrayList<Float> = ArrayList()

    var paint: Paint = Paint()
    var axisPaint:Paint = Paint()

    init {

        paint.color = Color.RED
        paint.strokeWidth = 5f

        axisPaint.color = Color.DKGRAY
        axisPaint.strokeWidth = 5f

    }

    fun setGraphArray(gpxDocument: GPXDocument) {

        for( point in gpxDocument.gpsPoints){
            // add speed in km/h to graphArrayY
            pointsY.add(point.speed * 3.6f)

        }

    }

    // overridden draw function
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        // call the superclass drawing function before we start
        super.onDraw(canvas)

        if (pointsY == null) {
            return;
        }


        // get the width and height of the canvas which is the available drawing area we have
        val width: Int = canvas!!.width
        val height: Int = canvas!!.height


        // fill the entire canvas
       //canvas.drawColor(Color.GRAY)

        canvas.drawLine(
            40.0f, height.toFloat() - 40.0f, width.toFloat() - 40.0f, height.toFloat()- 40.0f,
            axisPaint)

        canvas.drawLine(
            40.0f, 40.0f, 40.0f, height.toFloat() - 40.0f,
            axisPaint)


        val maxX = pointsY.size * 1.0f
        val maxY = 1000.0f

        val factorX = width / maxX;
        val factorY = height / maxY;


        for (i in 1 until pointsY.size) {

            val x0 = i - 1
            val y0 = pointsY[i-1]

            val x1 = i
            val y1 = pointsY[i]

            val sx = x0 * factorX
            val sy = height - y0 * factorY

            val ex = x1*factorX;
            val ey = height - y1 * factorY

            canvas!!.drawLine(sx, sy, ex, ey, paint)
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