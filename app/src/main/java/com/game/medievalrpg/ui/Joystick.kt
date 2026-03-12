package com.game.medievalrpg.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import kotlin.math.sqrt

class Joystick(
    private val centerX: Float,
    private val centerY: Float,
    private val outerRadius: Float = 100f,
    private val innerRadius: Float = 45f,
    private val deadZone: Float = 15f
) {
    private var thumbX: Float = centerX
    private var thumbY: Float = centerY
    private var isActive: Boolean = false
    private var pointerId: Int = -1

    var directionX: Float = 0f
        private set
    var directionY: Float = 0f
        private set
    val magnitude: Float get() {
        val dx = directionX; val dy = directionY
        return sqrt(dx * dx + dy * dy)
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 3f
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val idx = event.actionIndex
                val tx = event.getX(idx); val ty = event.getY(idx)
                if (!isActive && isInJoystickArea(tx, ty)) {
                    pointerId = event.getPointerId(idx)
                    isActive = true
                    updateThumb(tx, ty)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isActive) {
                    val ptrIdx = event.findPointerIndex(pointerId)
                    if (ptrIdx >= 0) updateThumb(event.getX(ptrIdx), event.getY(ptrIdx))
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val idx = event.actionIndex
                if (event.getPointerId(idx) == pointerId) {
                    reset(); return true
                }
            }
            MotionEvent.ACTION_CANCEL -> reset()
        }
        return false
    }

    private fun updateThumb(tx: Float, ty: Float) {
        val dx = tx - centerX; val dy = ty - centerY
        val dist = sqrt(dx * dx + dy * dy)
        if (dist > deadZone) {
            val clampedDist = minOf(dist, outerRadius)
            val nx = dx / dist; val ny = dy / dist
            thumbX = centerX + nx * clampedDist
            thumbY = centerY + ny * clampedDist
            directionX = nx * (clampedDist / outerRadius)
            directionY = ny * (clampedDist / outerRadius)
        } else {
            thumbX = centerX; thumbY = centerY
            directionX = 0f; directionY = 0f
        }
    }

    private fun reset() {
        isActive = false; pointerId = -1
        thumbX = centerX; thumbY = centerY
        directionX = 0f; directionY = 0f
    }

    private fun isInJoystickArea(tx: Float, ty: Float): Boolean {
        val dx = tx - centerX; val dy = ty - centerY
        return sqrt(dx * dx + dy * dy) <= outerRadius * 1.5f
    }

    fun draw(canvas: Canvas) {
        // Outer ring
        bgPaint.color = 0x44FFFFFF
        canvas.drawCircle(centerX, centerY, outerRadius, bgPaint)
        borderPaint.color = 0x99FFFFFF.toInt()
        canvas.drawCircle(centerX, centerY, outerRadius, borderPaint)

        // Inner dead zone ring
        borderPaint.color = 0x55FFFFFF
        canvas.drawCircle(centerX, centerY, deadZone, borderPaint)

        // Thumb
        thumbPaint.color = if (isActive) 0xCCFFFFFF.toInt() else 0x88FFFFFF.toInt()
        canvas.drawCircle(thumbX, thumbY, innerRadius, thumbPaint)
        borderPaint.color = 0xBBFFFFFF.toInt()
        canvas.drawCircle(thumbX, thumbY, innerRadius, borderPaint)

        // Direction indicator
        if (isActive && magnitude > 0.1f) {
            val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xAAFFD700.toInt(); style = Paint.Style.STROKE; strokeWidth = 3f
            }
            canvas.drawLine(centerX, centerY,
                centerX + directionX * outerRadius * 0.6f,
                centerY + directionY * outerRadius * 0.6f, arrowPaint)
        }
    }
}
