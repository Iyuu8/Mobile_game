package com.game.medievalrpg.world

import com.game.medievalrpg.entities.Player

class World(startZoneType: ZoneType = ZoneType.VILLAGE) {

    private val zones = mutableMapOf<ZoneType, Zone>()
    var currentZoneType: ZoneType = startZoneType
        private set
    val currentZone: Zone get() = zones[currentZoneType]!!

    var onZoneChanged: ((ZoneType) -> Unit)? = null

    init {
        loadZone(startZoneType)
    }

    private fun loadZone(type: ZoneType) {
        if (!zones.containsKey(type)) {
            zones[type] = Zone.createZone(type)
        }
    }

    fun changeZone(type: ZoneType, player: Player) {
        loadZone(type)
        currentZoneType = type
        val spawnPos = getSpawnPosition(type)
        player.x = spawnPos.first
        player.y = spawnPos.second
        onZoneChanged?.invoke(type)
    }

    private fun getSpawnPosition(type: ZoneType): Pair<Float, Float> = when (type) {
        ZoneType.VILLAGE      -> Pair(300f, 400f)
        ZoneType.FOREST       -> Pair(200f, 700f)
        ZoneType.CAVE         -> Pair(200f, 400f)
        ZoneType.CASTLE       -> Pair(1400f, 1500f)
        ZoneType.DRAGONS_LAIR -> Pair(300f, 800f)
        ZoneType.GRAVEYARD    -> Pair(200f, 400f)
        ZoneType.FROZEN_CAVES -> Pair(200f, 400f)
    }

    fun update(deltaTime: Float, player: Player) {
        currentZone.update(deltaTime, player.centerX, player.centerY)
    }

    fun getZone(type: ZoneType): Zone? = zones[type]

    fun getAdjacentZones(current: ZoneType): List<ZoneType> = when (current) {
        ZoneType.VILLAGE      -> listOf(ZoneType.FOREST, ZoneType.GRAVEYARD)
        ZoneType.FOREST       -> listOf(ZoneType.VILLAGE, ZoneType.CAVE)
        ZoneType.CAVE         -> listOf(ZoneType.FOREST, ZoneType.CASTLE)
        ZoneType.GRAVEYARD    -> listOf(ZoneType.VILLAGE, ZoneType.FROZEN_CAVES)
        ZoneType.FROZEN_CAVES -> listOf(ZoneType.GRAVEYARD, ZoneType.CASTLE)
        ZoneType.CASTLE       -> listOf(ZoneType.CAVE, ZoneType.FROZEN_CAVES, ZoneType.DRAGONS_LAIR)
        ZoneType.DRAGONS_LAIR -> listOf(ZoneType.CASTLE)
    }
}
