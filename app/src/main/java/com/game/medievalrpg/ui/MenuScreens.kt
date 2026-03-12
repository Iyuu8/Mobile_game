package com.game.medievalrpg.ui

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.game.medievalrpg.GameActivity
import com.game.medievalrpg.classes.*
import com.game.medievalrpg.data.GameState

enum class MenuState {
    MAIN, CLASS_SELECT, SETTINGS, PAUSE, GAME_OVER, VICTORY, QUEST_LOG
}

class MenuScreens(context: Context) : View(context) {

    var menuState: MenuState = MenuState.MAIN
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val classes = listOf(
        Knight(), Bandit(), Necromancer(), Wizard(), ShadowKing()
    )
    private var selectedClassIndex = 0

    var onResume: (() -> Unit)? = null
    var onSave: (() -> Unit)? = null
    var onMainMenu: (() -> Unit)? = null
    var onQuestLogClose: (() -> Unit)? = null

    private val buttonRects = mutableMapOf<String, RectF>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(0xFF1A0A00.toInt())

        when (menuState) {
            MenuState.MAIN        -> drawMainMenu(canvas)
            MenuState.CLASS_SELECT -> drawClassSelect(canvas)
            MenuState.SETTINGS    -> drawSettings(canvas)
            MenuState.PAUSE       -> drawPauseMenu(canvas)
            MenuState.GAME_OVER   -> drawGameOver(canvas)
            MenuState.VICTORY     -> drawVictory(canvas)
            MenuState.QUEST_LOG   -> drawQuestLog(canvas)
        }
    }

    private fun drawMainMenu(canvas: Canvas) {
        val cx = width / 2f
        // Title
        textPaint.textSize = 58f; textPaint.color = 0xFFFFD700.toInt()
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("MEDIEVAL RPG", cx, height * 0.18f, textPaint)
        textPaint.textSize = 22f; textPaint.color = 0xFFAA8833.toInt()
        canvas.drawText("Shadows of the Realm", cx, height * 0.25f, textPaint)

        // Decorative line
        paint.color = 0xFF8B6914.toInt(); paint.strokeWidth = 2f; paint.style = Paint.Style.STROKE
        canvas.drawLine(cx - 200f, height * 0.29f, cx + 200f, height * 0.29f, paint)
        paint.style = Paint.Style.FILL

        drawMenuButton(canvas, "new_game", "New Game", cx, height * 0.42f)
        drawMenuButton(canvas, "continue", "Continue", cx, height * 0.52f,
            enabled = GameState.hasSave(context))
        drawMenuButton(canvas, "settings", "Settings", cx, height * 0.62f)
        drawMenuButton(canvas, "quit",     "Quit",     cx, height * 0.72f, danger = true)

        textPaint.textSize = 14f; textPaint.color = 0xFF666666.toInt()
        canvas.drawText("v1.0  |  A 2D Medieval Fantasy RPG", cx, height * 0.92f, textPaint)
    }

    private fun drawClassSelect(canvas: Canvas) {
        val cx = width / 2f
        textPaint.textSize = 38f; textPaint.color = 0xFFFFD700.toInt(); textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("Choose Your Class", cx, height * 0.1f, textPaint)

        val cardW = minOf(width / 5.5f, 160f); val cardH = height * 0.55f
        val totalW = cardW * classes.size + 20f * (classes.size - 1)
        val startX = (width - totalW) / 2f

        classes.forEachIndexed { i, cls ->
            val cardX = startX + i * (cardW + 20f)
            val cardY = height * 0.15f
            val isSelected = i == selectedClassIndex
            val rect = RectF(cardX, cardY, cardX + cardW, cardY + cardH)
            buttonRects["class_$i"] = rect

            paint.color = if (isSelected) 0xFF331800.toInt() else 0xFF1A0D00.toInt()
            canvas.drawRoundRect(rect, 10f, 10f, paint)
            paint.color = if (isSelected) cls.secondaryColor else 0xFF554433.toInt()
            paint.style = Paint.Style.STROKE; paint.strokeWidth = if (isSelected) 3f else 1.5f
            canvas.drawRoundRect(rect, 10f, 10f, paint)
            paint.style = Paint.Style.FILL

            // Class color block
            paint.color = cls.primaryColor
            canvas.drawRect(cardX + 10f, cardY + 10f, cardX + cardW - 10f, cardY + 80f, paint)

            textPaint.textSize = 14f; textPaint.color = if (isSelected) 0xFFFFD700.toInt() else Color.WHITE
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(cls.name, cardX + cardW / 2, cardY + 100f, textPaint)

            // Stats
            textPaint.textSize = 11f; textPaint.color = 0xFFAAAAAA.toInt()
            canvas.drawText("HP: ${cls.baseHp}", cardX + cardW / 2, cardY + 120f, textPaint)
            canvas.drawText("ATK: ${cls.baseAttack}", cardX + cardW / 2, cardY + 135f, textPaint)
            canvas.drawText("DEF: ${cls.baseDefense}", cardX + cardW / 2, cardY + 150f, textPaint)
            canvas.drawText("MAG: ${cls.baseMagicPower}", cardX + cardW / 2, cardY + 165f, textPaint)
            canvas.drawText("SPD: ${cls.baseSpeed}", cardX + cardW / 2, cardY + 180f, textPaint)

            // Skills
            textPaint.textSize = 10f; textPaint.color = 0xFF8888CC.toInt()
            cls.skills.forEachIndexed { si, skill ->
                canvas.drawText("• ${skill.name}", cardX + cardW / 2, cardY + 205f + si * 16f, textPaint)
            }

            // Description (truncated)
            textPaint.textSize = 9f; textPaint.color = 0xFF888888.toInt()
            cls.description.chunked(18).take(3).forEachIndexed { li, line ->
                canvas.drawText(line, cardX + cardW / 2, cardY + 280f + li * 13f, textPaint)
            }
        }

        // Description of selected class
        val sel = classes[selectedClassIndex]
        textPaint.textSize = 15f; textPaint.color = 0xFFCCCCCC.toInt(); textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(sel.description, cx, height * 0.78f, textPaint)

        drawMenuButton(canvas, "start_game", "Start Adventure!", cx, height * 0.88f)
        drawMenuButton(canvas, "back_main",  "← Back",          cx, height * 0.95f, small = true)
    }

    private fun drawSettings(canvas: Canvas) {
        val cx = width / 2f
        textPaint.textSize = 38f; textPaint.color = 0xFFFFD700.toInt(); textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("Settings", cx, height * 0.12f, textPaint)
        textPaint.textSize = 18f; textPaint.color = 0xFFAAAAAA.toInt()
        canvas.drawText("Music: On/Off  |  SFX: On/Off", cx, height * 0.35f, textPaint)
        canvas.drawText("(Audio loaded from assets in full build)", cx, height * 0.45f, textPaint)
        drawMenuButton(canvas, "back_main", "← Back", cx, height * 0.75f)
    }

    private fun drawPauseMenu(canvas: Canvas) {
        val cx = width / 2f
        paint.color = 0xCC000000.toInt()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        textPaint.textSize = 42f; textPaint.color = 0xFFFFD700.toInt(); textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("PAUSED", cx, height * 0.22f, textPaint)
        drawMenuButton(canvas, "resume",    "Resume",      cx, height * 0.38f)
        drawMenuButton(canvas, "save",      "Save Game",   cx, height * 0.48f)
        drawMenuButton(canvas, "main_menu", "Main Menu",   cx, height * 0.58f, danger = true)
    }

    private fun drawGameOver(canvas: Canvas) {
        val cx = width / 2f
        paint.color = 0xCC880000.toInt()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        textPaint.textSize = 52f; textPaint.color = 0xFFCC0000.toInt(); textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("GAME OVER", cx, height * 0.3f, textPaint)
        textPaint.textSize = 20f; textPaint.color = 0xFFAAAAAA.toInt()
        canvas.drawText("You have fallen in battle...", cx, height * 0.42f, textPaint)
        drawMenuButton(canvas, "new_game",  "Try Again",  cx, height * 0.58f)
        drawMenuButton(canvas, "main_menu", "Main Menu",  cx, height * 0.68f)
    }

    private fun drawVictory(canvas: Canvas) {
        val cx = width / 2f
        paint.color = 0xCC001100.toInt()
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        textPaint.textSize = 52f; textPaint.color = 0xFFFFD700.toInt(); textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("VICTORY!", cx, height * 0.25f, textPaint)
        textPaint.textSize = 20f; textPaint.color = Color.WHITE
        canvas.drawText("You have defeated the Shadow King!", cx, height * 0.4f, textPaint)
        canvas.drawText("The realm is at peace once more.", cx, height * 0.48f, textPaint)
        drawMenuButton(canvas, "new_game",  "Play Again",  cx, height * 0.65f)
        drawMenuButton(canvas, "main_menu", "Main Menu",   cx, height * 0.75f)
    }

    private fun drawQuestLog(canvas: Canvas) {
        val cx = width / 2f
        textPaint.textSize = 30f; textPaint.color = 0xFFFFD700.toInt(); textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("Quest Log", cx, 40f, textPaint)
        textPaint.textSize = 16f; textPaint.color = 0xFFAAAAAA.toInt()
        canvas.drawText("(Quest details appear here in-game)", cx, height / 2f, textPaint)
        drawMenuButton(canvas, "close_quest_log", "← Close", cx, height * 0.85f, small = true)
    }

    private fun drawMenuButton(canvas: Canvas, id: String, label: String, cx: Float, cy: Float,
                                enabled: Boolean = true, danger: Boolean = false, small: Boolean = false) {
        val bw = if (small) 180f else 260f; val bh = if (small) 40f else 52f
        val rect = RectF(cx - bw / 2, cy - bh / 2, cx + bw / 2, cy + bh / 2)
        buttonRects[id] = rect

        paint.color = when {
            !enabled -> 0x55333333.toInt()
            danger -> 0xAA550000.toInt()
            else -> 0xAA2A1800.toInt()
        }
        canvas.drawRoundRect(rect, 8f, 8f, paint)

        paint.color = when {
            !enabled -> 0xFF444444.toInt()
            danger -> 0xFFAA2222.toInt()
            else -> 0xFF8B6914.toInt()
        }
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 1.5f
        canvas.drawRoundRect(rect, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        textPaint.textSize = if (small) 16f else 20f
        textPaint.color = if (!enabled) Color.GRAY else Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(label, cx, cy + 7f, textPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return true
        val tx = event.x; val ty = event.y

        buttonRects.forEach { (id, rect) ->
            if (rect.contains(tx, ty)) handleButtonPress(id)
        }
        // Class card selection
        if (menuState == MenuState.CLASS_SELECT) {
            classes.indices.forEach { i ->
                buttonRects["class_$i"]?.let { rect ->
                    if (rect.contains(tx, ty)) {
                        selectedClassIndex = i
                        GameState.instance.selectedClass = classes[i].name
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    private fun handleButtonPress(id: String) {
        when (id) {
            "new_game" -> {
                if (menuState == MenuState.MAIN || menuState == MenuState.GAME_OVER || menuState == MenuState.VICTORY) {
                    menuState = MenuState.CLASS_SELECT; invalidate()
                } else {
                    GameState.reset()
                    context.startActivity(Intent(context, GameActivity::class.java))
                }
            }
            "continue" -> {
                if (GameState.hasSave(context)) {
                    context.startActivity(Intent(context, GameActivity::class.java)
                        .putExtra("load_save", true))
                }
            }
            "settings"        -> { menuState = MenuState.SETTINGS; invalidate() }
            "start_game"      -> {
                GameState.instance.selectedClass = classes[selectedClassIndex].name
                GameState.instance.isNewGame = true
                context.startActivity(Intent(context, GameActivity::class.java))
            }
            "back_main"       -> { menuState = MenuState.MAIN; invalidate() }
            "resume"          -> onResume?.invoke()
            "save"            -> onSave?.invoke()
            "main_menu"       -> onMainMenu?.invoke()
            "close_quest_log" -> { onQuestLogClose?.invoke(); invalidate() }
            "quit"            -> (context as? android.app.Activity)?.finish()
        }
    }

    fun setState(state: MenuState) { menuState = state; invalidate() }
}
