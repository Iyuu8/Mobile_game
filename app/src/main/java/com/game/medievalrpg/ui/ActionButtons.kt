package com.game.medievalrpg.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import kotlin.math.sqrt

data class ActionButton(
    val id: String,
    val label: String,
    val x: Float,
    val y: Float,
    val radius: Float = 55f,
    val color: Int = 0xAA4444AA.toInt(),
    var isPressed: Boolean = false,
    var cooldownPercent: Float = 0f,
    var pointerId: Int = -1
)

class ActionButtons(screenWidth: Int, screenHeight: Int) {

    val buttons = mutableListOf<ActionButton>()
    var onButtonPressed: ((String) -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; textAlign = Paint.Align.CENTER; textSize = 16f
    }

    init {
        val bx = screenWidth.toFloat()
        val by = screenHeight.toFloat()
        // Attack button - large, lower right
        buttons.add(ActionButton("attack",   "ATK", bx - 90f,  by - 120f, 60f, 0xAA882222.toInt()))
        // Skill buttons
        buttons.add(ActionButton("skill1",   "SK1", bx - 190f, by - 130f, 45f, 0xAA225588.toInt()))
        buttons.add(ActionButton("skill2",   "SK2", bx - 90f,  by - 240f, 45f, 0xAA226688.toInt()))
        buttons.add(ActionButton("skill3",   "SK3", bx - 190f, by - 240f, 45f, 0xAA224488.toInt()))
        // Interact button
        buttons.add(ActionButton("interact", " ! ", bx - 290f, by - 130f, 40f, 0xAA228822.toInt()))
        // Top right buttons
        buttons.add(ActionButton("inventory","INV", bx - 60f,  80f,       36f, 0xAA446644.toInt()))
        buttons.add(ActionButton("pause",    "||",  bx - 60f,  30f,       28f, 0xAA444444.toInt()))
        buttons.add(ActionButton("quests",   "QST", bx - 120f, 80f,       36f, 0xAA664444.toInt()))
    }

    fun onTouchEvent(event: MotionEvent): String? {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val idx = event.actionIndex
                val tx = event.getX(idx); val ty = event.getY(idx)
                val pid = event.getPointerId(idx)
                buttons.forEach { btn ->
                    val dx = tx - btn.x; val dy = ty - btn.y
                    if (sqrt(dx * dx + dy * dy) <= btn.radius && btn.pointerId == -1) {
                        btn.isPressed = true
                        btn.pointerId = pid
                        onButtonPressed?.invoke(btn.id)
                        return btn.id
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val idx = event.actionIndex
                val pid = event.getPointerId(idx)
                buttons.filter { it.pointerId == pid }.forEach { btn ->
                    btn.isPressed = false; btn.pointerId = -1
                }
            }
            MotionEvent.ACTION_CANCEL -> { buttons.forEach { it.isPressed = false; it.pointerId = -1 } }
        }
        return null
    }

    fun updateCooldowns(skillCooldownPercents: List<Float>) {
        skillCooldownPercents.forEachIndexed { i, pct ->
            buttons.find { it.id == "skill${i + 1}" }?.cooldownPercent = pct
        }
    }

    fun draw(canvas: Canvas) {
        buttons.forEach { btn -> drawButton(canvas, btn) }
    }

    private fun drawButton(canvas: Canvas, btn: ActionButton) {
        val pressScale = if (btn.isPressed) 0.88f else 1.0f
        val r = btn.radius * pressScale

        // Shadow
        paint.color = 0x55000000
        canvas.drawCircle(btn.x + 3f, btn.y + 3f, r, paint)

        // Background
        paint.color = if (btn.isPressed) darken(btn.color) else btn.color
        canvas.drawCircle(btn.x, btn.y, r, paint)

        // Border
        paint.color = 0x88FFFFFF.toInt()
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f
        canvas.drawCircle(btn.x, btn.y, r, paint)
        paint.style = Paint.Style.FILL

        // Cooldown overlay (arc)
        if (btn.cooldownPercent > 0f) {
            paint.color = 0xAA000000.toInt()
            val rect = RectF(btn.x - r, btn.y - r, btn.x + r, btn.y + r)
            canvas.drawArc(rect, -90f, 360f * btn.cooldownPercent, true, paint)
        }

        // Label
        textPaint.textSize = if (btn.id == "attack") 20f else 14f
        textPaint.color = if (btn.cooldownPercent > 0f) Color.GRAY else Color.WHITE
        canvas.drawText(btn.label, btn.x, btn.y + 5f, textPaint)
    }

    private fun darken(color: Int): Int {
        val r = ((color shr 16 and 0xFF) * 0.6f).toInt()
        val g = ((color shr 8  and 0xFF) * 0.6f).toInt()
        val b = ((color        and 0xFF) * 0.6f).toInt()
        val a = (color shr 24 and 0xFF)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
