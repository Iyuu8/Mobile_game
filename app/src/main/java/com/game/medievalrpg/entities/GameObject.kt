package com.game.medievalrpg.entities

import android.graphics.Canvas
import android.graphics.RectF

abstract class GameObject(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float
) {
    var velocityX: Float = 0f
    var velocityY: Float = 0f
    var isActive: Boolean = true
    var facingRight: Boolean = true

    val bounds: RectF get() = RectF(x, y, x + width, y + height)
    val centerX: Float get() = x + width / 2f
    val centerY: Float get() = y + height / 2f

    abstract fun update(deltaTime: Float)
    abstract fun draw(canvas: Canvas, offsetX: Float, offsetY: Float)

    fun distanceTo(other: GameObject): Float {
        val dx = centerX - other.centerX
        val dy = centerY - other.centerY
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    fun overlaps(other: GameObject): Boolean = bounds.intersect(other.bounds)

    fun move(deltaTime: Float) {
        x += velocityX * deltaTime
        y += velocityY * deltaTime
        if (velocityX > 0) facingRight = true
        else if (velocityX < 0) facingRight = false
    }
}
