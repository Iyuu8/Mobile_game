package com.game.medievalrpg.quests

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.game.medievalrpg.entities.NPC
import com.game.medievalrpg.entities.Dialogue
import com.game.medievalrpg.entities.DialogueOption

class DialogueSystem {

    private var currentNPC: NPC? = null
    private var currentDialogue: Dialogue? = null
    var isActive: Boolean = false

    private val paint = Paint()
    private val bgRect = RectF()

    var onAction: ((String) -> Unit)? = null
    var onClose: (() -> Unit)? = null

    fun startDialogue(npc: NPC) {
        currentNPC = npc
        npc.isInteracting = true
        currentDialogue = npc.getCurrentDialogue()
        isActive = true
    }

    fun closeDialogue() {
        currentNPC?.isInteracting = false
        currentNPC?.resetDialogue()
        currentNPC = null
        currentDialogue = null
        isActive = false
        onClose?.invoke()
    }

    fun selectOption(index: Int) {
        val npc = currentNPC ?: return
        val dialogue = currentDialogue ?: return
        val option = dialogue.options.getOrNull(index) ?: return

        if (option.action.isNotEmpty()) onAction?.invoke(option.action)

        if (option.nextDialogueIndex >= 0) {
            npc.selectOption(index)
            currentDialogue = npc.getCurrentDialogue()
        } else {
            closeDialogue()
        }
    }

    fun draw(canvas: Canvas, screenWidth: Int, screenHeight: Int) {
        if (!isActive) return
        val dialogue = currentDialogue ?: return
        val npc = currentNPC ?: return

        val boxH = 220f
        val boxY = screenHeight - boxH - 20f
        val margin = 30f

        // Background
        paint.color = 0xDD1A0A00.toInt()
        bgRect.set(margin, boxY, screenWidth - margin, screenHeight - 20f)
        canvas.drawRoundRect(bgRect, 12f, 12f, paint)

        // Border
        paint.color = 0xFFDAA520.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRoundRect(bgRect, 12f, 12f, paint)
        paint.style = Paint.Style.FILL

        // NPC name
        paint.color = 0xFFFFD700.toInt()
        paint.textSize = 22f
        canvas.drawText(npc.name, margin + 16f, boxY + 30f, paint)

        // Dialogue text (word-wrap simplified)
        paint.color = Color.WHITE
        paint.textSize = 17f
        val maxWidth = screenWidth - margin * 2 - 32f
        val words = dialogue.text.split(" ")
        var line = ""; var lineY = boxY + 60f
        words.forEach { word ->
            val test = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(test) > maxWidth) {
                canvas.drawText(line, margin + 16f, lineY, paint)
                line = word; lineY += 22f
            } else line = test
        }
        if (line.isNotEmpty()) canvas.drawText(line, margin + 16f, lineY, paint)

        // Options
        val optionsStartY = boxY + 130f
        dialogue.options.forEachIndexed { i, option ->
            paint.color = 0xFFAAAAAA.toInt()
            paint.textSize = 16f
            canvas.drawText("[${i + 1}] ${option.text}", margin + 16f, optionsStartY + i * 26f, paint)
        }

        // Close hint
        paint.color = 0xFF888888.toInt()
        paint.textSize = 14f
        canvas.drawText("Tap option to select", screenWidth - margin - 180f, screenHeight - 30f, paint)
    }

    fun handleTap(x: Float, y: Float, screenWidth: Int, screenHeight: Int): Boolean {
        if (!isActive) return false
        val dialogue = currentDialogue ?: return false
        val boxH = 220f
        val boxY = screenHeight - boxH - 20f
        val optionsStartY = boxY + 130f

        dialogue.options.forEachIndexed { i, _ ->
            val optionY = optionsStartY + i * 26f
            if (y >= optionY - 20f && y <= optionY + 6f) {
                selectOption(i)
                return true
            }
        }
        return true // Consume touch while dialogue is open
    }
}
