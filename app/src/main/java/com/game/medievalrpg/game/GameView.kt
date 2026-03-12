package com.game.medievalrpg.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.game.medievalrpg.quests.DialogueSystem

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    val engine = GameEngine(context)
    private var gameLoop: GameLoop? = null
    private val dialogueSystem = DialogueSystem()
    private val bgPaint = Paint()

    var onPauseRequested: (() -> Unit)? = null
    var onGameOver: (() -> Unit)? = null
    var onVictory: (() -> Unit)? = null
    var onQuestLog: (() -> Unit)? = null

    init {
        holder.addCallback(this)
        isFocusable = true

        dialogueSystem.onAction = { action -> handleDialogueAction(action) }
        dialogueSystem.onClose  = { /* nothing extra */ }
    }

    override fun surfaceCreated(h: SurfaceHolder) {
        val sw = width; val sh = height
        engine.initialize(sw, sh)

        engine.onPause   = { onPauseRequested?.invoke() }
        engine.onGameOver = { onGameOver?.invoke() }
        engine.onVictory  = { onVictory?.invoke() }
        engine.onQuestLog = { onQuestLog?.invoke() }

        gameLoop = GameLoop(this).also { it.startLoop() }
    }

    override fun surfaceChanged(h: SurfaceHolder, format: Int, w: Int, h2: Int) {
        engine.screenWidth = w; engine.screenHeight = h2
    }

    override fun surfaceDestroyed(h: SurfaceHolder) {
        gameLoop?.stopLoop()
        gameLoop = null
        engine.soundManager.release()
    }

    fun update(deltaTime: Float) {
        if (!engine.isPaused) engine.update(deltaTime)
    }

    fun render() {
        val canvas = holder.lockCanvas() ?: return
        try {
            drawFrame(canvas)
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawFrame(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        if (!engine.isRunning) return

        val zone = engine.world.currentZone
        val cam = engine.camera

        // Draw world
        zone.draw(canvas, cam.x, cam.y, width, height)

        // Draw player
        engine.player.draw(canvas, cam.x, cam.y)

        // Draw damage numbers
        engine.renderer.drawDamageNumbers(canvas, engine.combatSystem)

        // Draw boss health bar if boss present
        zone.bossEnemy?.let { if (!it.isDead) engine.renderer.drawBossHealthBar(canvas, it, width) }

        // Draw zone transition overlay
        val transAlpha = engine.getZoneTransitionAlpha()
        if (transAlpha > 0f) engine.renderer.drawZoneTransitionEffect(canvas, transAlpha, width, height)

        // Draw UI
        if (!engine.inventoryUI.isVisible) {
            engine.joystick.draw(canvas)
            engine.actionButtons.draw(canvas)
            engine.hud.draw(canvas, engine.player)
        }

        engine.inventoryUI.draw(canvas, engine.player.inventory)

        // Draw dialogue
        dialogueSystem.draw(canvas, width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!engine.isRunning) return false

        // Inventory UI takes priority
        if (engine.inventoryUI.isVisible) {
            engine.inventoryUI.handleTouch(event, engine.player.inventory)
            return true
        }

        // Dialogue system
        if (dialogueSystem.isActive) {
            dialogueSystem.handleTap(event.x, event.y, width, height)
            return true
        }

        // Check NPC interaction tap
        if (event.action == MotionEvent.ACTION_DOWN) {
            val zone = engine.world.currentZone
            zone.npcs.filter { it.isInteracting }.firstOrNull()?.let { npc ->
                val (sx, sy) = engine.camera.worldToScreen(npc.x, npc.y)
                val dx = event.x - (sx + npc.width / 2); val dy = event.y - (sy + npc.height / 2)
                if (Math.sqrt((dx * dx + dy * dy).toDouble()) < 80.0) {
                    dialogueSystem.startDialogue(npc)
                    return true
                }
            }
        }

        // Route to joystick and buttons
        val joystickHandled = engine.joystick.onTouchEvent(event)
        engine.actionButtons.onTouchEvent(event)
        return true
    }

    private fun handleDialogueAction(action: String) {
        when {
            action.startsWith("ACCEPT_QUEST_") -> {
                val questId = action.removePrefix("ACCEPT_QUEST_")
                engine.questManager.startQuest(questId)
                engine.addNotification("Quest Accepted!")
            }
            action == "OPEN_SHOP" -> engine.addNotification("Shop not available here.")
            action == "HEAL_PLAYER" -> {
                engine.player.heal(engine.player.maxHp)
                engine.addNotification("Fully healed!")
            }
        }
    }

    fun pauseGame() { engine.pause() }
    fun resumeGame() { engine.resume() }
    fun saveGame() { engine.saveGame() }
}
