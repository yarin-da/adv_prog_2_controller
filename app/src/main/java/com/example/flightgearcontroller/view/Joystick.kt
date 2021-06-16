package com.example.flightgearcontroller.view


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class Joystick @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    fun interface ChangeNotifier {
        fun onChange(x: Float, y: Float)
    }

    var notifier: ChangeNotifier? = null
    private var radius = 0.0f
    private var center = PointF((width / 2).toFloat(), (height / 2).toFloat())
    private var position = PointF((width / 2).toFloat(), (height / 2).toFloat())
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        isClickable = true
    }

    private fun recenterKnob() {
        position = PointF((width / 2).toFloat(), (height / 2).toFloat())
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        invalidate()
        return true
    }

    // update the circle's values when the widget changes size
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(w, h) / 2).toFloat()
        center = PointF((width / 2).toFloat(), (height / 2).toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        if (position.x == 0.0f && position.y == 0.0f) recenterKnob()
        paint.color = Color.parseColor("#232F34")
        canvas?.drawCircle(center.x, center.y, radius, paint)
        paint.color = Color.parseColor("#F9AA33")
        canvas?.drawCircle(position.x, position.y, 5 * radius / 11, paint)
    }

    // return the new position
    private fun getPosition(x: Float?, y: Float?): PointF {
        val r = radius - (5 * radius / 11)
        if (x == null || y == null) {
            return PointF(r, r)
        }
        val diffX = x - center.x
        val diffY = y - center.y
        val length = min(sqrt(diffX * diffX + diffY * diffY), r)
        val angle = atan2(diffX, diffY) - 3.141592654 / 2
        val newX = center.x + length * cos(angle)
        val newY = center.y - length * sin(angle)
        return PointF(newX.toFloat(), newY.toFloat())
    }

    private fun notifyPosition(event: MotionEvent) {
        val y = event.y
        val x = event.x
        val newPos = getPosition(x, y)

        val action = event.action
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            // update the position and notify normalized values
            position = newPos
            val diffX = position.x - center.x
            val diffY = position.y - center.y
            val length = sqrt(diffX * diffX + diffY * diffY)
            val notifyX = diffX / length
            // flip y values
            val notifyY = -1 * diffY / length
            notifier?.onChange(notifyX, notifyY)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { notifyPosition(it) }
        return performClick()
    }
}
