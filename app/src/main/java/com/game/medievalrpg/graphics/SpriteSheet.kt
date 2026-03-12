package com.game.medievalrpg.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class SpriteSheet(
    private val bitmap: Bitmap,
    val frameWidth: Int,
    val frameHeight: Int
) {
    val cols: Int = bitmap.width / frameWidth
    val rows: Int = bitmap.height / frameHeight
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun getFrame(row: Int, col: Int): Rect {
        return Rect(
            col * frameWidth,
            row * frameHeight,
            col * frameWidth + frameWidth,
            row * frameHeight + frameHeight
        )
    }

    fun draw(canvas: Canvas, row: Int, col: Int, destRect: Rect) {
        canvas.drawBitmap(bitmap, getFrame(row, col), destRect, paint)
    }

    fun draw(canvas: Canvas, frameIndex: Int, destRect: Rect) {
        val row = frameIndex / cols
        val col = frameIndex % cols
        draw(canvas, row, col, destRect)
    }

    companion object {
        /**
         * Creates a simple placeholder sprite sheet using colored rectangles.
         * In a real project, this would load from assets.
         */
        fun createPlaceholder(width: Int, height: Int, frameW: Int, frameH: Int, color: Int): SpriteSheet {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = color
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            // Draw grid lines
            paint.color = 0x33000000
            paint.strokeWidth = 1f
            var x = 0f
            while (x <= width) { canvas.drawLine(x, 0f, x, height.toFloat(), paint); x += frameW }
            var y = 0f
            while (y <= height) { canvas.drawLine(0f, y, width.toFloat(), y, paint); y += frameH }
            return SpriteSheet(bitmap, frameW, frameH)
        }
    }
}
