package com.game.medievalrpg.world

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.game.medievalrpg.entities.Enemy
import com.game.medievalrpg.entities.EnemyType
import com.game.medievalrpg.entities.NPC

import kotlin.random.Random

enum class ZoneType(
    val displayName: String,
    val recommendedLevel: Int,
    val musicKey: String,
    val ambientColor: Int
) {
    VILLAGE("Millhaven Village", 1, "village", 0xFF87CEEB.toInt()),
    FOREST("Dark Forest", 3, "forest", 0xFF228B22.toInt()),
    CAVE("Goblin Caves", 6, "cave", 0xFF696969.toInt()),
    CASTLE("Shadow Castle", 12, "castle", 0xFF1A1A2E.toInt()),
    DRAGONS_LAIR("Dragon's Lair", 18, "dragons_lair", 0xFFFF4500.toInt()),
    GRAVEYARD("Ancient Graveyard", 9, "graveyard", 0xFF2F4F2F.toInt()),
    FROZEN_CAVES("Frozen Depths", 15, "frozen", 0xFF87CEEB.toInt())
}

class Zone(
    val type: ZoneType,
    val tileMap: TileMap
) {
    val enemies = mutableListOf<Enemy>()
    val npcs = mutableListOf<NPC>()
    val chestLocations = mutableListOf<Pair<Float, Float>>()
    val openedChests = mutableSetOf<String>()

    var bossEnemy: Enemy? = null

    private val paint = Paint()
    private val tilePaint = Paint()

    fun addEnemy(enemy: Enemy) { enemies.add(enemy) }
    fun addNPC(npc: NPC) { npcs.add(npc) }

    fun getActiveEnemies(): List<Enemy> = enemies.filter { !it.isDead && it.isActive }
    fun getAliveEnemies(): List<Enemy> = enemies.filter { !it.isDead }
    fun areAllEnemiesDefeated(): Boolean = enemies.all { it.isDead }

    fun update(deltaTime: Float, playerX: Float, playerY: Float) {
        enemies.forEach { enemy ->
            if (!enemy.isDead) {
                enemy.update(deltaTime)
                enemy.updateAI(playerX, playerY, deltaTime)
            }
        }
        npcs.forEach { npc -> npc.update(deltaTime) }
    }

    fun draw(canvas: Canvas, camOffsetX: Float, camOffsetY: Float,
             screenWidth: Int, screenHeight: Int) {
        drawTileMap(canvas, camOffsetX, camOffsetY, screenWidth, screenHeight)
        npcs.forEach { it.draw(canvas, camOffsetX, camOffsetY) }
        enemies.forEach { if (!it.isDead) it.draw(canvas, camOffsetX, camOffsetY) }
        bossEnemy?.let { if (!it.isDead) it.draw(canvas, camOffsetX, camOffsetY) }
    }

    private fun drawTileMap(canvas: Canvas, offsetX: Float, offsetY: Float,
                             screenWidth: Int, screenHeight: Int) {
        val ts = tileMap.tileSize.toFloat()
        val startCol = maxOf(0, (offsetX / ts).toInt() - 1)
        val endCol   = minOf(tileMap.width - 1, ((offsetX + screenWidth) / ts).toInt() + 1)
        val startRow = maxOf(0, (offsetY / ts).toInt() - 1)
        val endRow   = minOf(tileMap.height - 1, ((offsetY + screenHeight) / ts).toInt() + 1)

        for (r in startRow..endRow) {
            for (c in startCol..endCol) {
                val tile = tileMap[r, c] ?: continue
                val left = c * ts - offsetX
                val top  = r * ts - offsetY
                tilePaint.color = tile.color
                canvas.drawRect(left, top, left + ts, top + ts, tilePaint)

                // Grid lines for stone/wall tiles
                if (tile.solid || tile == TileType.FLOOR_STONE) {
                    tilePaint.color = (tile.color and 0x00FFFFFF) or 0x22000000
                    canvas.drawRect(left, top, left + ts, top + 2, tilePaint)
                    canvas.drawRect(left, top, left + 2, top + ts, tilePaint)
                }

                // Special tiles
                if (tile.isChest && !openedChests.contains("$r,$c")) {
                    tilePaint.color = 0xFFFFD700.toInt()
                    canvas.drawRect(left + 8, top + 12, left + ts - 8, top + ts - 8, tilePaint)
                    tilePaint.color = 0xFF8B4513.toInt()
                    canvas.drawRect(left + 10, top + 14, left + ts - 10, top + ts - 10, tilePaint)
                }
                if (tile.isDoor) {
                    tilePaint.color = 0xFF8B4513.toInt()
                    canvas.drawRect(left + 6, top + 4, left + ts - 6, top + ts, tilePaint)
                    tilePaint.color = 0xFFDAA520.toInt()
                    canvas.drawCircle(left + ts * 0.7f, top + ts * 0.5f, 4f, tilePaint)
                }
            }
        }
    }

    fun isSolidAt(worldX: Float, worldY: Float): Boolean = tileMap.isSolid(worldX, worldY)

    fun checkChestAt(worldX: Float, worldY: Float): Pair<Int, Int>? {
        val ts = tileMap.tileSize
        val col = (worldX / ts).toInt()
        val row = (worldY / ts).toInt()
        val key = "$row,$col"
        if (tileMap[row, col] == TileType.CHEST && !openedChests.contains(key)) {
            return Pair(row, col)
        }
        return null
    }

    fun openChest(row: Int, col: Int) { openedChests.add("$row,$col") }

    companion object {
        fun createZone(type: ZoneType): Zone {
            val tileMap = when (type) {
                ZoneType.VILLAGE      -> TileMap.generateVillage()
                ZoneType.FOREST       -> TileMap.generateForest()
                ZoneType.CAVE         -> TileMap.generateCave()
                ZoneType.CASTLE       -> TileMap.generateCastle()
                ZoneType.DRAGONS_LAIR -> TileMap.generateDragonsLair()
                ZoneType.GRAVEYARD    -> TileMap.generateGraveyard()
                ZoneType.FROZEN_CAVES -> TileMap.generateFrozenCaves()
            }
            val zone = Zone(type, tileMap)
            spawnEntities(zone, type)
            return zone
        }

        private fun spawnEntities(zone: Zone, type: ZoneType) {
            when (type) {
                ZoneType.VILLAGE -> {
                    NPC.createVillageNPCs().forEach { zone.addNPC(it) }
                }
                ZoneType.FOREST -> {
                    spawnEnemies(zone, EnemyType.GOBLIN_SCOUT, 4, 3)
                    spawnEnemies(zone, EnemyType.GOBLIN_WARRIOR, 3, 3)
                    spawnEnemies(zone, EnemyType.WOLF, 5, 3)
                    spawnEnemies(zone, EnemyType.ORC_GRUNT, 3, 4)
                    zone.bossEnemy = Enemy(1800f, 600f, EnemyType.GOBLIN_CHIEF, 4)
                }
                ZoneType.CAVE -> {
                    spawnEnemies(zone, EnemyType.GOBLIN_WARRIOR, 5, 5)
                    spawnEnemies(zone, EnemyType.GOBLIN_SHAMAN, 3, 5)
                    spawnEnemies(zone, EnemyType.ORC_GRUNT, 4, 6)
                    spawnEnemies(zone, EnemyType.SKELETON_SOLDIER, 4, 5)
                    zone.bossEnemy = Enemy(1500f, 800f, EnemyType.ORC_WARLORD, 7)
                }
                ZoneType.GRAVEYARD -> {
                    spawnEnemies(zone, EnemyType.SKELETON_SOLDIER, 6, 8)
                    spawnEnemies(zone, EnemyType.SKELETON_ARCHER, 4, 8)
                    spawnEnemies(zone, EnemyType.SKELETON_MAGE, 3, 9)
                    spawnEnemies(zone, EnemyType.SKELETON_KNIGHT, 3, 10)
                    zone.bossEnemy = Enemy(1200f, 500f, EnemyType.VAMPIRE, 11)
                }
                ZoneType.FROZEN_CAVES -> {
                    spawnEnemies(zone, EnemyType.ICE_TROLL, 4, 14)
                    spawnEnemies(zone, EnemyType.SKELETON_KNIGHT, 4, 13)
                    spawnEnemies(zone, EnemyType.DIRE_WOLF, 5, 13)
                    spawnEnemies(zone, EnemyType.SHADOW_WOLF, 3, 14)
                    zone.bossEnemy = Enemy(1800f, 800f, EnemyType.ELDER_TROLL, 16)
                }
                ZoneType.CASTLE -> {
                    spawnEnemies(zone, EnemyType.DARK_KNIGHT, 5, 12)
                    spawnEnemies(zone, EnemyType.SKELETON_KNIGHT, 4, 13)
                    spawnEnemies(zone, EnemyType.DEMON, 3, 14)
                    spawnEnemies(zone, EnemyType.VAMPIRE, 3, 13)
                    zone.bossEnemy = Enemy(2000f, 900f, EnemyType.LICH, 18)
                }
                ZoneType.DRAGONS_LAIR -> {
                    spawnEnemies(zone, EnemyType.FIRE_DRAGON, 2, 18)
                    spawnEnemies(zone, EnemyType.ICE_DRAGON, 2, 18)
                    spawnEnemies(zone, EnemyType.YOUNG_DRAGON, 3, 17)
                    spawnEnemies(zone, EnemyType.DEMON, 4, 17)
                    zone.bossEnemy = Enemy(2200f, 1000f, EnemyType.ANCIENT_DRAGON, 22)
                }
            }
        }

        private fun spawnEnemies(zone: Zone, type: EnemyType, count: Int, level: Int) {
            val ts = zone.tileMap.tileSize.toFloat()
            val maxX = zone.tileMap.getPixelWidth() - 200f
            val maxY = zone.tileMap.getPixelHeight() - 200f
            repeat(count) {
                var x: Float; var y: Float
                var tries = 0
                do {
                    x = 200f + Random.nextFloat() * (maxX - 200f)
                    y = 200f + Random.nextFloat() * (maxY - 200f)
                    tries++
                } while (zone.tileMap.isSolid(x, y) && tries < 30)
                zone.addEnemy(Enemy(x, y, type, level))
            }
        }
    }
}
