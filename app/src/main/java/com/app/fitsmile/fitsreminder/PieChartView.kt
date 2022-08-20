package com.app.fitsmile.fitsreminder

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat


/**
 * @author Yuana andhikayuana@gmail.com
 * @since Aug, Sun 05 2018 17.23
 **/
class PieChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var slicePaint: Paint = Paint()
    private var centerPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var sliceColors: IntArray = intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
    private var rectF: RectF? = null
    private var dataPoints: FloatArray = floatArrayOf()

    init {
        slicePaint.isAntiAlias = true
        slicePaint.isDither = true
        slicePaint.setStyle(Paint.Style.FILL_AND_STROKE);
     /*   slicePaint.setStrokeCap(Paint.Cap.ROUND)
        slicePaint.setStrokeWidth(dpToPx(20).toFloat())*/
        centerPaint.color = Color.WHITE
        centerPaint.style = Paint.Style.FILL
    }

    private fun scale(): FloatArray {
        var scaledValues = FloatArray(dataPoints.size)
        for (i in dataPoints.indices) {
            scaledValues.fill((dataPoints[i] / getTotal()) * 360, i, dataPoints.size)
        }
        return scaledValues
    }
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val startTop = 0F
        val startLeft = 0F
        val endBottom = width.toFloat()
        val endRight = endBottom

        rectF = RectF(startLeft, startTop, endRight, endBottom)

        val scaledValues = scale()
        var sliceStartPoint = 270F

        for (i in scaledValues.indices) {
            slicePaint.color = ContextCompat.getColor(context, sliceColors[i])
            canvas!!.drawArc(rectF!!, sliceStartPoint, scaledValues[i], true, slicePaint)
         //   canvas!!.drawArc(rectF!!, sliceStartPoint, scaledValues[i], false, slicePaint)
            sliceStartPoint += scaledValues[i]
        }


        val centerX = (measuredWidth / 2).toFloat()
        val centerY = (measuredHeight / 2).toFloat()
        val radius = Math.min(centerX, centerY)

        canvas!!.drawCircle(centerX, centerY, radius - 50, centerPaint)

    }

    fun getTotal(): Float = dataPoints.sum()

    fun setDataPoints(data: FloatArray) {
        dataPoints = data
        invalidateAndRequestLayout()
    }

    fun setCenterColor(colorId: Int) {
        centerPaint.color = ContextCompat.getColor(context, colorId)
        invalidateAndRequestLayout()
    }

    fun setSliceColor(colors: IntArray) {
        sliceColors = colors
        invalidateAndRequestLayout()
    }

    private fun invalidateAndRequestLayout() {
        invalidate()
        requestLayout()
    }
}