package com.game.medievalrpg.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.game.medievalrpg.combat.CombatSystem
import com.game.medievalrpg.entities.Enemy
import com.game.medievalrpg.entities.Player
import com.game.medievalrpg.world.Zone

class Renderer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun drawDamageNumbers(canvas: Canvas, combatSystem: CombatSystem) {
        combatSystem.getDamageNumbers().forEach { num ->
            val alpha = (num.timer * 255).toInt().coerceIn(0, 255)
            paint.color = num.color
            paint.alpha = alpha
            paint.textSize = 22f
            canvas.drawText(num.text, num.x, num.y, paint)
        }
        paint.alpha = 255
    }

    fun drawNotification(canvas: Canvas, text: String, x: Float, y: Float, alpha: Float) {
        paint.color = Color.WHITE
        paint.alpha = (alpha * 255).toInt()
        paint.textSize = 28f
        val tw = paint.measureText(text)
        paint.color = 0xAA000000.toInt()
        canvas.drawRoundRect(x - tw / 2 - 12f, y - 32f, x + tw / 2 + 12f, y + 8f, 8f, 8f, paint)
        paint.color = 0xFFFFD700.toInt()
        paint.alpha = (alpha * 255).toInt()
        canvas.drawText(text, x - tw / 2, y, paint)
        paint.alpha = 255
    }

    fun drawBossHealthBar(canvas: Canvas, boss: Enemy, screenWidth: Int) {
        if (boss.isDead) return
        val barW = screenWidth * 0.6f
        val barH = 20f
        val barX = screenWidth * 0.2f
        val barY = 20f
        paint.color = 0xAA000000.toInt()
        canvas.drawRoundRect(barX - 2, barY - 2, barX + barW + 2, barY + barH + 2, 4f, 4f, paint)
        paint.color = 0xFF330000.toInt()
        canvas.drawRect(barX, barY, barX + barW, barY + barH, paint)
        paint.color = 0xFFCC0000.toInt()
        canvas.drawRect(barX, barY, barX + barW * boss.getHpPercent(), barY + barH, paint)
        paint.color = Color.WHITE
        paint.textSize = 15f
        val label = "${boss.type.displayName}  ${boss.hp}/${boss.maxHp}"
        canvas.drawText(label, barX + 6f, barY + 14f, paint)
    }

    fun drawZoneTransitionEffect(canvas: Canvas, alpha: Float, screenWidth: Int, screenHeight: Int) {
        paint.color = Color.BLACK
        paint.alpha = (alpha * 255).toInt()
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)
        paint.alpha = 255
    }
}
