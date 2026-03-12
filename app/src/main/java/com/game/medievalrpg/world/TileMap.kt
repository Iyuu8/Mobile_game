package com.game.medievalrpg.world

enum class TileType(
    val solid: Boolean,
    val color: Int,
    val isWater: Boolean = false,
    val isLava: Boolean = false,
    val isDoor: Boolean = false,
    val isChest: Boolean = false
) {
    GRASS(false,   0xFF4A7C3F.toInt()),
    STONE(false,   0xFF808080.toInt()),
    DIRT(false,    0xFF8B6914.toInt()),
    SAND(false,    0xFFE8C87A.toInt()),
    WATER(true,    0xFF1E6FCC.toInt(), isWater = true),
    LAVA(true,     0xFFFF4500.toInt(), isLava = true),
    WALL(true,     0xFF404040.toInt()),
    WALL_STONE(true, 0xFF505050.toInt()),
    FLOOR_STONE(false, 0xFF6A6A6A.toInt()),
    FLOOR_WOOD(false,  0xFF8B5E3C.toInt()),
    DOOR(false,    0xFF6B3A2A.toInt(), isDoor = true),
    CHEST(false,   0xFFDAA520.toInt(), isChest = true),
    SNOW(false,    0xFFEEEEFF.toInt()),
    ICE(false,     0xFFADD8E6.toInt()),
    GRAVE(false,   0xFF505050.toInt()),
    DARK_FLOOR(false, 0xFF1A1A2E.toInt()),
    PATH(false,    0xFFAA9977.toInt())
}

class TileMap(val width: Int, val height: Int) {

    val tileSize: Int = 64
    private val tiles: Array<Array<TileType>> = Array(height) { Array(width) { TileType.GRASS } }

    operator fun get(row: Int, col: Int): TileType? {
        if (row < 0 || row >= height || col < 0 || col >= width) return null
        return tiles[row][col]
    }

    operator fun set(row: Int, col: Int, type: TileType) {
        if (row < 0 || row >= height || col < 0 || col >= width) return
        tiles[row][col] = type
    }

    fun setRegion(rowStart: Int, rowEnd: Int, colStart: Int, colEnd: Int, type: TileType) {
        for (r in rowStart..rowEnd) for (c in colStart..colEnd) set(r, c, type)
    }

    fun getTileAt(worldX: Float, worldY: Float): TileType? {
        val col = (worldX / tileSize).toInt()
        val row = (worldY / tileSize).toInt()
        return get(row, col)
    }

    fun isSolid(worldX: Float, worldY: Float): Boolean {
        return getTileAt(worldX, worldY)?.solid ?: true
    }

    fun getPixelWidth(): Int = width * tileSize
    fun getPixelHeight(): Int = height * tileSize

    companion object {
        fun generateVillage(): TileMap {
            val map = TileMap(30, 20)
            // Fill with grass
            map.setRegion(0, 19, 0, 29, TileType.GRASS)
            // Stone paths
            for (c in 0..29) { map[8, c] = TileType.PATH; map[12, c] = TileType.PATH }
            for (r in 0..19) { map[r, 14] = TileType.PATH }
            // Buildings (wall outlines)
            for (c in 2..6) { map[2, c] = TileType.WALL; map[6, c] = TileType.WALL }
            for (r in 2..6) { map[r, 2] = TileType.WALL; map[r, 6] = TileType.WALL }
            map[4, 4] = TileType.DOOR
            map.setRegion(3, 5, 3, 5, TileType.FLOOR_WOOD)

            for (c in 18..23) { map[2, c] = TileType.WALL; map[6, c] = TileType.WALL }
            for (r in 2..6) { map[r, 18] = TileType.WALL; map[r, 23] = TileType.WALL }
            map[4, 20] = TileType.DOOR
            map.setRegion(3, 5, 19, 22, TileType.FLOOR_WOOD)

            for (c in 8..12) { map[14, c] = TileType.WALL; map[18, c] = TileType.WALL }
            for (r in 14..18) { map[r, 8] = TileType.WALL; map[r, 12] = TileType.WALL }
            map[16, 10] = TileType.DOOR
            map.setRegion(15, 17, 9, 11, TileType.FLOOR_WOOD)

            // Water pond
            map.setRegion(14, 17, 20, 26, TileType.WATER)
            // Chest
            map[10, 27] = TileType.CHEST
            return map
        }

        fun generateForest(): TileMap {
            val map = TileMap(40, 30)
            map.setRegion(0, 29, 0, 39, TileType.GRASS)
            for (c in 0..39) map[14, c] = TileType.PATH
            // Stone walls (ruins)
            for (c in 5..9) { map[5, c] = TileType.WALL_STONE; map[9, c] = TileType.WALL_STONE }
            for (r in 5..9) { map[r, 5] = TileType.WALL_STONE; map[r, 9] = TileType.WALL_STONE }
            // Water areas
            map.setRegion(18, 23, 8, 14, TileType.WATER)
            map.setRegion(3, 7, 28, 35, TileType.WATER)
            // Chest
            map[10, 22] = TileType.CHEST
            map[22, 32] = TileType.CHEST
            return map
        }

        fun generateCave(): TileMap {
            val map = TileMap(35, 25)
            map.setRegion(0, 24, 0, 34, TileType.WALL)
            // Carve out cave
            map.setRegion(2, 22, 2, 32, TileType.FLOOR_STONE)
            // Pillars
            for (r in listOf(5, 10, 15, 20)) for (c in listOf(6, 12, 18, 24, 30)) map[r, c] = TileType.WALL_STONE
            // Lava pools
            map.setRegion(8, 12, 14, 20, TileType.LAVA)
            // Paths around lava
            for (c in 13..21) { map[7, c] = TileType.STONE; map[13, c] = TileType.STONE }
            // Chests
            map[3, 30] = TileType.CHEST
            map[20, 6] = TileType.CHEST
            return map
        }

        fun generateCastle(): TileMap {
            val map = TileMap(45, 35)
            map.setRegion(0, 34, 0, 44, TileType.DARK_FLOOR)
            // Outer walls
            for (c in 0..44) { map[0, c] = TileType.WALL; map[34, c] = TileType.WALL }
            for (r in 0..34) { map[r, 0] = TileType.WALL; map[r, 44] = TileType.WALL }
            // Inner walls
            for (c in 8..36) { map[8, c] = TileType.WALL_STONE; map[26, c] = TileType.WALL_STONE }
            for (r in 8..26) { map[r, 8] = TileType.WALL_STONE; map[r, 36] = TileType.WALL_STONE }
            // Doors
            map[8, 22] = TileType.DOOR; map[17, 8] = TileType.DOOR; map[17, 36] = TileType.DOOR
            // Floor
            map.setRegion(9, 25, 9, 35, TileType.FLOOR_STONE)
            // Throne room
            map.setRegion(3, 7, 15, 29, TileType.FLOOR_STONE)
            // Chests
            map[4, 16] = TileType.CHEST; map[4, 28] = TileType.CHEST
            map[20, 10] = TileType.CHEST; map[20, 34] = TileType.CHEST
            return map
        }

        fun generateGraveyard(): TileMap {
            val map = TileMap(32, 24)
            map.setRegion(0, 23, 0, 31, TileType.DARK_FLOOR)
            for (c in 0..31) { map[0, c] = TileType.WALL; map[23, c] = TileType.WALL }
            for (r in 0..23) { map[r, 0] = TileType.WALL; map[r, 31] = TileType.WALL }
            // Graves
            for (r in listOf(4, 8, 12, 16, 20)) for (c in listOf(4, 8, 12, 16, 20, 24, 28)) map[r, c] = TileType.GRAVE
            map[5, 28] = TileType.CHEST
            return map
        }

        fun generateFrozenCaves(): TileMap {
            val map = TileMap(38, 28)
            map.setRegion(0, 27, 0, 37, TileType.SNOW)
            map.setRegion(2, 25, 2, 35, TileType.ICE)
            // Ice walls
            for (c in listOf(6, 14, 22, 30)) for (r in 4..22) {
                if (r % 4 != 0) map[r, c] = TileType.WALL_STONE
            }
            // Water pools (frozen)
            map.setRegion(10, 14, 16, 20, TileType.ICE)
            map[5, 34] = TileType.CHEST; map[22, 4] = TileType.CHEST
            return map
        }

        fun generateDragonsLair(): TileMap {
            val map = TileMap(40, 30)
            map.setRegion(0, 29, 0, 39, TileType.STONE)
            map.setRegion(2, 27, 2, 37, TileType.DARK_FLOOR)
            // Lava moat around center
            map.setRegion(10, 20, 14, 26, TileType.LAVA)
            map.setRegion(12, 18, 16, 24, TileType.DARK_FLOOR)
            // Bridges over lava
            for (r in 12..18) map[r, 14] = TileType.STONE
            for (r in 12..18) map[r, 26] = TileType.STONE
            for (c in 14..26) map[10, c] = TileType.STONE
            for (c in 14..26) map[20, c] = TileType.STONE
            map[14, 36] = TileType.CHEST; map[14, 3] = TileType.CHEST
            return map
        }
    }
}
