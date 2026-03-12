package com.game.medievalrpg.entities

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

enum class NPCType(val displayName: String, val color: Int) {
    VILLAGER("Villager", 0xFFFFCC99.toInt()),
    BLACKSMITH("Blacksmith", 0xFF8B4513.toInt()),
    MERCHANT("Merchant", 0xFF20B2AA.toInt()),
    QUEST_GIVER("Quest Giver", 0xFFFFD700.toInt()),
    INNKEEPER("Innkeeper", 0xFFDAA520.toInt()),
    WIZARD_NPC("Wizard", 0xFF4169E1.toInt()),
    GUARD("Guard", 0xFFC0C0C0.toInt()),
    ELDER("Elder", 0xFFDEB887.toInt()),
    CHILD("Child", 0xFFFFCC99.toInt())
}

data class Dialogue(
    val text: String,
    val options: List<DialogueOption> = emptyList()
)

data class DialogueOption(
    val text: String,
    val nextDialogueIndex: Int = -1,
    val action: String = ""
)

class NPC(
    x: Float,
    y: Float,
    val npcType: NPCType,
    val name: String,
    val dialogues: List<Dialogue>,
    val questId: String? = null
) : GameObject(x, y, 40f, 56f) {

    private var dialogueIndex: Int = 0
    private var animTimer: Float = 0f
    private val paint = Paint()
    var isInteracting: Boolean = false
    val interactRange: Float = 90f

    override fun update(deltaTime: Float) {
        animTimer += deltaTime
        // idle bob animation placeholder
    }

    override fun draw(canvas: Canvas, offsetX: Float, offsetY: Float) {
        val sx = x - offsetX; val sy = y - offsetY

        // Body
        paint.color = npcType.color
        canvas.drawRect(sx + 6, sy + 20, sx + width - 6, sy + height, paint)

        // Head
        paint.color = 0xFFFFCC99.toInt()
        canvas.drawOval(sx + 8, sy + 2, sx + width - 8, sy + 26, paint)

        // Hat / identifier
        paint.color = when (npcType) {
            NPCType.BLACKSMITH   -> 0xFF8B4513.toInt()
            NPCType.MERCHANT     -> 0xFF20B2AA.toInt()
            NPCType.QUEST_GIVER  -> 0xFFFFD700.toInt()
            NPCType.WIZARD_NPC   -> 0xFF4169E1.toInt()
            else -> 0xFF8B8682.toInt()
        }
        canvas.drawRect(sx + 6, sy, sx + width - 6, sy + 10, paint)

        // Quest indicator (!)
        if (questId != null) {
            paint.color = 0xFFFFD700.toInt()
            paint.textSize = 20f
            canvas.drawText("!", sx + width / 2 - 5, sy - 5, paint)
        }

        // Interact indicator
        if (isInteracting) {
            paint.color = Color.WHITE
            paint.textSize = 14f
            canvas.drawText("[ Talk ]", sx - 8, sy - 18f, paint)
        }

        // Name
        paint.color = Color.WHITE
        paint.textSize = 14f
        canvas.drawText(name, sx - (name.length * 4f), sy - 3, paint)
    }

    fun getCurrentDialogue(): Dialogue? = dialogues.getOrNull(dialogueIndex)

    fun selectOption(index: Int) {
        val current = getCurrentDialogue() ?: return
        val option = current.options.getOrNull(index) ?: return
        if (option.nextDialogueIndex >= 0) dialogueIndex = option.nextDialogueIndex
        else dialogueIndex = 0
    }

    fun resetDialogue() { dialogueIndex = 0 }

    fun isPlayerInRange(playerX: Float, playerY: Float): Boolean {
        val dx = centerX - playerX; val dy = centerY - playerY
        return Math.sqrt((dx * dx + dy * dy).toDouble()) <= interactRange
    }

    companion object {
        fun createVillageNPCs(): List<NPC> = listOf(
            NPC(200f, 200f, NPCType.ELDER, "Elder Aldric",
                listOf(
                    Dialogue("Welcome, adventurer! Our village is in grave danger.", listOf(
                        DialogueOption("What danger?", 1),
                        DialogueOption("Goodbye.", -1)
                    )),
                    Dialogue("Goblins have been raiding our farms. Their chief leads them from the eastern forest.", listOf(
                        DialogueOption("I'll deal with them.", 2),
                        DialogueOption("Not my problem.", -1)
                    )),
                    Dialogue("Thank you! Defeat the Goblin Chief and we'll reward you handsomely.", listOf(
                        DialogueOption("I'm on it.", -1, "ACCEPT_QUEST_goblin_menace")
                    ))
                ), questId = "goblin_menace"
            ),
            NPC(350f, 250f, NPCType.BLACKSMITH, "Gareth the Smith",
                listOf(
                    Dialogue("I can upgrade your equipment if you bring me the right materials.", listOf(
                        DialogueOption("What do you need?", 1),
                        DialogueOption("Just browsing.", -1)
                    )),
                    Dialogue("Iron ore, wolf pelts, and goblin teeth all fetch a good price here.", listOf(
                        DialogueOption("Thanks.", -1)
                    ))
                )
            ),
            NPC(450f, 300f, NPCType.MERCHANT, "Mira the Merchant",
                listOf(
                    Dialogue("I have goods from all corners of the realm! Browse my wares!", listOf(
                        DialogueOption("Show me what you have.", -1, "OPEN_SHOP"),
                        DialogueOption("No thanks.", -1)
                    ))
                )
            ),
            NPC(600f, 200f, NPCType.INNKEEPER, "Old Tom",
                listOf(
                    Dialogue("Welcome to the Rusty Flagon! A room and meal will restore your strength.", listOf(
                        DialogueOption("I'll rest here. (Heal HP)", -1, "HEAL_PLAYER"),
                        DialogueOption("Just passing through.", -1)
                    ))
                )
            )
        )
    }
}
