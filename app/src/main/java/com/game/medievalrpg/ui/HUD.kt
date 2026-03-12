package com.game.medievalrpg.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.game.medievalrpg.entities.Player
import com.game.medievalrpg.world.ZoneType

class HUD(private val screenWidth: Int, private val screenHeight: Int) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val barBg = RectF(); private val barFg = RectF()

    private var notificationText: String = ""
    private var notificationTimer: Float = 0f
    private val notificationDuration = 2.5f

    private var zoneText: String = ""
    private var zoneTimer: Float = 0f
    private val zoneDuration = 3.0f

    private var levelUpText: String = ""
    private var levelUpTimer: Float = 0f

    fun update(deltaTime: Float) {
        if (notificationTimer > 0f) notificationTimer -= deltaTime
        if (zoneTimer > 0f) zoneTimer -= deltaTime
        if (levelUpTimer > 0f) levelUpTimer -= deltaTime
    }

    fun showNotification(text: String) { notificationText = text; notificationTimer = notificationDuration }
    fun showZoneEntered(zone: ZoneType) { zoneText = "Entering: ${zone.displayName}"; zoneTimer = zoneDuration }
    fun showLevelUp(level: Int) { levelUpText = "LEVEL UP! → $level"; levelUpTimer = 3.0f }

    fun draw(canvas: Canvas, player: Player) {
        drawStatusBars(canvas, player)
        drawPlayerInfo(canvas, player)
        drawSkillCooldowns(canvas, player)
        drawNotifications(canvas)
    }

    private fun drawStatusBars(canvas: Canvas, player: Player) {
        val barW = 220f; val barH = 18f
        val bx = 16f; var by = 16f

        // HP bar
        paint.color = 0xBB111111.toInt()
        canvas.drawRoundRect(bx - 2, by - 2, bx + barW + 2, by + barH + 2, 4f, 4f, paint)
        paint.color = 0xFF330000.toInt()
        canvas.drawRect(bx, by, bx + barW, by + barH, paint)
        paint.color = lerp(0xFFCC0000.toInt(), 0xFF00CC00.toInt(), player.getHpPercent())
        canvas.drawRect(bx, by, bx + barW * player.getHpPercent(), by + barH, paint)
        textPaint.textSize = 13f; textPaint.color = Color.WHITE
        canvas.drawText("HP  ${player.hp} / ${player.maxHp}", bx + 6f, by + 13f, textPaint)
        by += barH + 6f

        // Mana bar
        paint.color = 0xBB111111.toInt()
        canvas.drawRoundRect(bx - 2, by - 2, bx + barW + 2, by + barH + 2, 4f, 4f, paint)
        paint.color = 0xFF000044.toInt()
        canvas.drawRect(bx, by, bx + barW, by + barH, paint)
        paint.color = 0xFF0044CC.toInt()
        canvas.drawRect(bx, by, bx + barW * player.getManaPercent(), by + barH, paint)
        textPaint.textSize = 13f
        canvas.drawText("MP  ${player.mana} / ${player.maxMana}", bx + 6f, by + 13f, textPaint)
        by += barH + 6f

        // EXP bar (thin)
        val expH = 8f
        paint.color = 0xBB111111.toInt()
        canvas.drawRect(bx, by, bx + barW, by + expH, paint)
        paint.color = 0xFFFFD700.toInt()
        canvas.drawRect(bx, by, bx + barW * player.getExpPercent(), by + expH, paint)
    }

    private fun drawPlayerInfo(canvas: Canvas, player: Player) {
        val bx = 16f; val by = 90f
        paint.color = 0xAA111111.toInt()
        canvas.drawRoundRect(bx - 2, by, bx + 160f, by + 52f, 6f, 6f, paint)
        textPaint.textSize = 14f; textPaint.color = 0xFFFFD700.toInt()
        canvas.drawText("Lv.${player.level}  ${player.characterClass.name}", bx + 6f, by + 18f, textPaint)
        textPaint.color = 0xFFFFCC44.toInt()
        canvas.drawText("Gold: ${player.gold}", bx + 6f, by + 36f, textPaint)
        textPaint.color = 0xFFAAAAAA.toInt(); textPaint.textSize = 12f
        canvas.drawText("ATK:${player.attack}  DEF:${player.defense}  SPD:${player.speed}", bx + 6f, by + 50f, textPaint)
    }

    private fun drawSkillCooldowns(canvas: Canvas, player: Player) {
        val startX = screenWidth / 2f - (player.skills.size * 60f) / 2f
        val y = screenHeight - 70f

        player.skills.forEachIndexed { i, skill ->
            val sx = startX + i * 62f
            // Background
            paint.color = 0xAA000000.toInt()
            canvas.drawRoundRect(sx, y, sx + 58f, y + 58f, 8f, 8f, paint)
            // Skill color
            paint.color = when (i) {
                0 -> 0xFF882222.toInt()
                1 -> 0xFF225588.toInt()
                2 -> 0xFF226688.toInt()
                else -> 0xFF224488.toInt()
            }
            canvas.drawRoundRect(sx + 2, y + 2, sx + 56f, y + 56f, 6f, 6f, paint)
            // Cooldown overlay
            if (!skill.isReady) {
                paint.color = 0xCC000000.toInt()
                val rect = RectF(sx + 2, y + 2, sx + 56f, y + 56f)
                canvas.drawArc(rect, -90f, 360f * skill.cooldownPercent(), true, paint)
                textPaint.color = Color.WHITE; textPaint.textSize = 12f
                canvas.drawText(String.format("%.1f", skill.cooldownCurrent), sx + 29f, y + 34f, textPaint)
            }
            // Skill label
            textPaint.color = if (skill.isReady) Color.WHITE else Color.GRAY
            textPaint.textSize = 11f
            val shortName = skill.name.take(6)
            canvas.drawText(shortName, sx + 29f, y + 48f, textPaint)
            // Number
            textPaint.textSize = 14f
            canvas.drawText("${i + 1}", sx + 8f, y + 16f, textPaint)
        }
    }

    private fun drawNotifications(canvas: Canvas) {
        val cx = screenWidth / 2f

        if (levelUpTimer > 0f) {
            val alpha = if (levelUpTimer > 2f) 1f else levelUpTimer / 2f
            paint.color = 0xCC000000.toInt(); paint.alpha = (alpha * 200).toInt()
            val tw = 260f
            canvas.drawRoundRect(cx - tw / 2, 100f, cx + tw / 2, 145f, 10f, 10f, paint)
            paint.alpha = 255
            textPaint.textSize = 26f
            textPaint.color = (((alpha * 255).toInt() shl 24) or 0xFFD700)
            val text = levelUpText
            canvas.drawText(text, cx - paint.measureText(text) / 2f, 135f, textPaint)
        }

        if (notificationTimer > 0f) {
            val alpha = minOf(1f, notificationTimer / 0.5f)
            textPaint.textSize = 20f
            textPaint.color = (((alpha * 230).toInt() shl 24) or 0xFFFFFF)
            val tw = textPaint.measureText(notificationText)
            paint.color = 0xAA000000.toInt(); paint.alpha = (alpha * 170).toInt()
            canvas.drawRoundRect(cx - tw / 2 - 10, screenHeight / 2f - 60f,
                cx + tw / 2 + 10, screenHeight / 2f - 30f, 8f, 8f, paint)
            paint.alpha = 255
            canvas.drawText(notificationText, cx - tw / 2, screenHeight / 2f - 38f, textPaint)
        }

        if (zoneTimer > 0f) {
            val alpha = if (zoneTimer > 2f) 1f else zoneTimer / 2f
            textPaint.textSize = 28f
            textPaint.color = (((alpha * 220).toInt() shl 24) or 0xFFD700)
            val tw = textPaint.measureText(zoneText)
            canvas.drawText(zoneText, cx - tw / 2, 56f, textPaint)
        }
    }

    private fun lerp(colorA: Int, colorB: Int, t: Float): Int {
        val clampT = t.coerceIn(0f, 1f)
        val ra = (colorA shr 16 and 0xFF); val ga = (colorA shr 8 and 0xFF); val ba = (colorA and 0xFF)
        val rb = (colorB shr 16 and 0xFF); val gb = (colorB shr 8 and 0xFF); val bb = (colorB and 0xFF)
        val r = (ra + (rb - ra) * clampT).toInt()
        val g = (ga + (gb - ga) * clampT).toInt()
        val b = (ba + (bb - ba) * clampT).toInt()
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }
}
