package com.game.medievalrpg.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import com.game.medievalrpg.items.*

class InventoryUI(private val screenWidth: Int, private val screenHeight: Int) {

    var isVisible: Boolean = false
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }

    private val panelW = screenWidth * 0.85f
    private val panelH = screenHeight * 0.85f
    private val panelX = (screenWidth - panelW) / 2f
    private val panelY = (screenHeight - panelH) / 2f

    private val cellSize = 70f
    private val cellPad = 8f
    private val cols = 5

    private var selectedItem: Item? = null
    private var scrollOffset: Int = 0

    var onEquipItem: ((Item) -> Unit)? = null
    var onDropItem: ((Item) -> Unit)? = null
    var onClose: (() -> Unit)? = null

    fun toggle() { isVisible = !isVisible; if (!isVisible) selectedItem = null }

    fun draw(canvas: Canvas, inventory: Inventory) {
        if (!isVisible) return

        // Dim background
        paint.color = 0xCC000000.toInt()
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)

        // Panel background
        paint.color = 0xFF1A1208.toInt()
        canvas.drawRoundRect(panelX, panelY, panelX + panelW, panelY + panelH, 14f, 14f, paint)
        paint.color = 0xFFDAA520.toInt()
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f
        canvas.drawRoundRect(panelX, panelY, panelX + panelW, panelY + panelH, 14f, 14f, paint)
        paint.style = Paint.Style.FILL

        // Title
        textPaint.textSize = 24f; textPaint.color = 0xFFFFD700.toInt()
        canvas.drawText("Inventory  (${inventory.size}/${inventory.maxCapacity})", panelX + 16f, panelY + 34f, textPaint)

        // Close button
        paint.color = 0xAA880000.toInt()
        canvas.drawCircle(panelX + panelW - 24f, panelY + 24f, 18f, paint)
        textPaint.textSize = 20f; textPaint.color = Color.WHITE
        canvas.drawText("X", panelX + panelW - 32f, panelY + 30f, textPaint)

        // Equipped section header
        textPaint.textSize = 16f; textPaint.color = 0xFFAAA044.toInt()
        canvas.drawText("EQUIPPED", panelX + 16f, panelY + 58f, textPaint)

        drawEquippedSlots(canvas, inventory)
        drawItemGrid(canvas, inventory)
        drawSelectedItemDetail(canvas)
    }

    private fun drawEquippedSlots(canvas: Canvas, inventory: Inventory) {
        val slots = listOf(
            "Weapon" to inventory.equippedWeapon,
            "Helmet" to inventory.equippedHelmet,
            "Chest"  to inventory.equippedChest,
            "Boots"  to inventory.equippedBoots,
            "Access" to inventory.equippedAccessory
        )
        slots.forEachIndexed { i, (label, item) ->
            val sx = panelX + 16f + i * (cellSize + cellPad)
            val sy = panelY + 65f
            val color = item?.rarityColor ?: 0xFF333333.toInt()
            paint.color = color and 0x00FFFFFF or 0x88000000.toInt()
            canvas.drawRoundRect(sx, sy, sx + cellSize, sy + cellSize, 6f, 6f, paint)
            paint.color = color
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f
            canvas.drawRoundRect(sx, sy, sx + cellSize, sy + cellSize, 6f, 6f, paint)
            paint.style = Paint.Style.FILL
            textPaint.textSize = 11f; textPaint.color = Color.GRAY
            canvas.drawText(label, sx + 4f, sy + 14f, textPaint)
            item?.let {
                textPaint.textSize = 10f; textPaint.color = Color.WHITE
                it.name.split(" ").take(2).forEachIndexed { li, word ->
                    canvas.drawText(word, sx + 4f, sy + 32f + li * 14f, textPaint)
                }
            }
        }
    }

    private fun drawItemGrid(canvas: Canvas, inventory: Inventory) {
        val gridStartX = panelX + 16f
        val gridStartY = panelY + 150f
        val items = inventory.getItems()

        textPaint.textSize = 14f; textPaint.color = 0xFFAAA044.toInt()
        canvas.drawText("BACKPACK", gridStartX, gridStartY - 4f, textPaint)

        items.forEachIndexed { i, item ->
            val row = i / cols; val col = i % cols
            val sx = gridStartX + col * (cellSize + cellPad)
            val sy = gridStartY + 8f + row * (cellSize + cellPad)
            if (sy > panelY + panelH - 80f) return

            val isSelected = item == selectedItem
            paint.color = if (isSelected) 0xAA443300.toInt() else 0xAA222222.toInt()
            canvas.drawRoundRect(sx, sy, sx + cellSize, sy + cellSize, 6f, 6f, paint)

            // Rarity border
            paint.color = item.rarityColor
            paint.style = Paint.Style.STROKE; paint.strokeWidth = if (isSelected) 3f else 1.5f
            canvas.drawRoundRect(sx, sy, sx + cellSize, sy + cellSize, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            // Item name (abbreviated)
            textPaint.textSize = 10f; textPaint.color = Color.WHITE
            item.name.split(" ").take(2).forEachIndexed { li, word ->
                canvas.drawText(word.take(8), sx + 4f, sy + 18f + li * 13f, textPaint)
            }
            // Type icon
            textPaint.textSize = 18f; textPaint.color = item.rarityColor
            val icon = when (item) { is Weapon -> "⚔"; is Armor -> "🛡"; else -> "?" }
            canvas.drawText(icon, sx + cellSize - 22f, sy + cellSize - 8f, textPaint)
        }
    }

    private fun drawSelectedItemDetail(canvas: Canvas) {
        val item = selectedItem ?: return
        val detailX = panelX + panelW - 220f; val detailY = panelY + 155f
        val detailW = 200f; val detailH = panelH - 175f

        paint.color = 0xAA110C00.toInt()
        canvas.drawRoundRect(detailX, detailY, detailX + detailW, detailY + detailH, 8f, 8f, paint)
        paint.color = item.rarityColor; paint.style = Paint.Style.STROKE; paint.strokeWidth = 1.5f
        canvas.drawRoundRect(detailX, detailY, detailX + detailW, detailY + detailH, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        textPaint.textSize = 15f; textPaint.color = item.rarityColor
        canvas.drawText(item.name, detailX + 8f, detailY + 22f, textPaint)

        val rarityName = item.rarity.name.lowercase().replaceFirstChar { it.uppercase() }
        textPaint.textSize = 12f; textPaint.color = item.rarityColor
        canvas.drawText(rarityName, detailX + 8f, detailY + 38f, textPaint)

        textPaint.textSize = 11f; textPaint.color = 0xFFAAAAAA.toInt()
        val desc = item.description; var dy = detailY + 58f
        desc.chunked(24).forEach { line -> canvas.drawText(line, detailX + 8f, dy, textPaint); dy += 15f }

        dy += 8f
        textPaint.color = Color.WHITE; textPaint.textSize = 12f
        when (item) {
            is Weapon -> {
                canvas.drawText("ATK +${item.attackBonus}", detailX + 8f, dy, textPaint); dy += 16f
                if (item.magicBonus > 0) { canvas.drawText("MAG +${item.magicBonus}", detailX + 8f, dy, textPaint); dy += 16f }
                if (item.speedBonus > 0) { canvas.drawText("SPD +${item.speedBonus}", detailX + 8f, dy, textPaint); dy += 16f }
                canvas.drawText("Crit: ${(item.critChance * 100).toInt()}%", detailX + 8f, dy, textPaint); dy += 16f
            }
            is Armor -> {
                canvas.drawText("DEF +${item.defenseBonus}", detailX + 8f, dy, textPaint); dy += 16f
                if (item.hpBonus > 0) { canvas.drawText("HP  +${item.hpBonus}", detailX + 8f, dy, textPaint); dy += 16f }
                if (item.magicBonus > 0) { canvas.drawText("MAG +${item.magicBonus}", detailX + 8f, dy, textPaint); dy += 16f }
                if (item.speedBonus > 0) { canvas.drawText("SPD +${item.speedBonus}", detailX + 8f, dy, textPaint); dy += 16f }
            }
        }

        dy = detailY + detailH - 70f
        canvas.drawText("Value: ${item.value}g", detailX + 8f, dy, textPaint)

        // Equip button
        paint.color = 0xAA004400.toInt()
        canvas.drawRoundRect(detailX + 8f, dy + 8f, detailX + detailW - 8f, dy + 32f, 6f, 6f, paint)
        textPaint.textSize = 13f; textPaint.color = Color.WHITE
        canvas.drawText("Equip", detailX + detailW / 2 - 20f, dy + 24f, textPaint)
    }

    fun handleTouch(event: MotionEvent, inventory: Inventory): Boolean {
        if (!isVisible) return false
        if (event.action != MotionEvent.ACTION_DOWN) return true

        val tx = event.x; val ty = event.y

        // Close button
        val closeX = panelX + panelW - 24f; val closeY = panelY + 24f
        if (dist(tx, ty, closeX, closeY) < 22f) { toggle(); onClose?.invoke(); return true }

        // Item grid tap
        val gridStartX = panelX + 16f; val gridStartY = panelY + 158f
        inventory.getItems().forEachIndexed { i, item ->
            val row = i / cols; val col = i % cols
            val sx = gridStartX + col * (cellSize + cellPad)
            val sy = gridStartY + row * (cellSize + cellPad)
            if (tx >= sx && tx <= sx + cellSize && ty >= sy && ty <= sy + cellSize) {
                selectedItem = if (selectedItem == item) null else item
                return true
            }
        }

        // Equip button for selected item
        selectedItem?.let { item ->
            val detailX = panelX + panelW - 220f
            val detailY = panelY + 155f + (panelH - 175f) - 70f + 8f
            val detailW = 200f
            if (tx >= detailX + 8f && tx <= detailX + detailW - 8f && ty >= detailY && ty <= detailY + 32f) {
                onEquipItem?.invoke(item)
                return true
            }
        }
        return true
    }

    private fun dist(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x1 - x2; val dy = y1 - y2
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}
