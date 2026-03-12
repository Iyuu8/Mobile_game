package com.game.medievalrpg.game

class Camera(
    var x: Float = 0f,
    var y: Float = 0f,
    var screenWidth: Int = 1280,
    var screenHeight: Int = 720
) {
    private val lerpFactor: Float = 5f

    fun update(targetX: Float, targetY: Float, mapWidth: Float, mapHeight: Float, deltaTime: Float) {
        val desiredX = targetX - screenWidth / 2f
        val desiredY = targetY - screenHeight / 2f
        x += (desiredX - x) * lerpFactor * deltaTime
        y += (desiredY - y) * lerpFactor * deltaTime
        clampToBounds(mapWidth, mapHeight)
    }

    fun snapTo(targetX: Float, targetY: Float, mapWidth: Float, mapHeight: Float) {
        x = targetX - screenWidth / 2f
        y = targetY - screenHeight / 2f
        clampToBounds(mapWidth, mapHeight)
    }

    private fun clampToBounds(mapWidth: Float, mapHeight: Float) {
        x = x.coerceIn(0f, maxOf(0f, mapWidth - screenWidth))
        y = y.coerceIn(0f, maxOf(0f, mapHeight - screenHeight))
    }

    fun isVisible(objX: Float, objY: Float, objW: Float, objH: Float): Boolean {
        return objX + objW > x && objX < x + screenWidth &&
               objY + objH > y && objY < y + screenHeight
    }

    fun worldToScreen(worldX: Float, worldY: Float): Pair<Float, Float> {
        return Pair(worldX - x, worldY - y)
    }

    fun screenToWorld(screenX: Float, screenY: Float): Pair<Float, Float> {
        return Pair(screenX + x, screenY + y)
    }
}
